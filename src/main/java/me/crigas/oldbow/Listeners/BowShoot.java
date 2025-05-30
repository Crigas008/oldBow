package me.crigas.oldbow.Listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.Collection;

public class BowShoot implements Listener {
    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (event.getForce() < 0.2) {
            if (event.getProjectile() instanceof AbstractArrow) {
                AbstractArrow originalArrow = (AbstractArrow) event.getProjectile();
                event.getProjectile().remove();

                if (!consumeArrow(player, originalArrow, event.getBow())) {

                    return;
                }

                Vector direction = player.getLocation().getDirection().normalize();
                Vector behind = direction.clone().multiply(-2f);
                Vector spawnPos = player.getLocation().toVector().add(behind).add(new Vector(0, 1, 0));

                AbstractArrow arrow;
                if (originalArrow instanceof SpectralArrow) {
                    arrow = player.getWorld().spawnArrow(
                            spawnPos.toLocation(player.getWorld()),
                            player.getLocation().toVector().add(new Vector(0, 1.5f, 0)).subtract(spawnPos).normalize(),
                            0.8f,
                            0f,
                            SpectralArrow.class
                    );
                } else {
                    arrow = player.getWorld().spawnArrow(
                            spawnPos.toLocation(player.getWorld()),
                            player.getLocation().toVector().add(new Vector(0, 1.5f, 0)).subtract(spawnPos).normalize(),
                            0.8f,
                            0f
                    );
                    if (originalArrow instanceof Arrow) {
                        ((Arrow) arrow).setBasePotionData(((Arrow) originalArrow).getBasePotionData());
                    }
                }
                arrow.setCritical(originalArrow.isCritical());
                arrow.setDamage(originalArrow.getDamage());
                arrow.setPierceLevel(originalArrow.getPierceLevel());
                arrow.setKnockbackStrength(originalArrow.getKnockbackStrength());
                arrow.setShooter(player);
                if (event.getBow() != null && event.getBow().containsEnchantment(org.bukkit.enchantments.Enchantment.ARROW_INFINITE)) {
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                }
                else {
                    arrow.setPickupStatus(originalArrow.getPickupStatus());
                }
            }
        }
    }

    private boolean consumeArrow(Player player, AbstractArrow arrow, ItemStack bow) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }

        Material arrowType = Material.ARROW;
        PotionType potionType = null;
        Collection<PotionEffect> customEffects = null;

        if (arrow instanceof SpectralArrow) {
            arrowType = Material.SPECTRAL_ARROW;
        } else if (arrow instanceof Arrow) {
            Arrow tipped = (Arrow) arrow;
            if (tipped.getBasePotionData().getType() != PotionType.UNCRAFTABLE || tipped.hasCustomEffects()) {
                arrowType = Material.TIPPED_ARROW;
                potionType = tipped.getBasePotionData().getType();
                customEffects = tipped.getCustomEffects();
            } else {
                if (bow != null && bow.containsEnchantment(org.bukkit.enchantments.Enchantment.ARROW_INFINITE)) {
                    return true;
                }
            }
        }

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != arrowType) continue;

            if (arrowType == Material.TIPPED_ARROW) {
                org.bukkit.inventory.meta.PotionMeta meta = (org.bukkit.inventory.meta.PotionMeta) item.getItemMeta();
                if (meta == null) continue;
                if (!meta.getBasePotionData().getType().equals(potionType)) continue;
                if (!meta.getCustomEffects().equals(customEffects)) continue;
            }

            item.setAmount(item.getAmount() - 1);
            return true;
        }

        return false;
    }
}