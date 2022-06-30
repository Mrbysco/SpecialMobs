package fathertoast.specialmobs.common.entity.skeleton;

import fathertoast.specialmobs.common.bestiary.BestiaryInfo;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.bestiary.SpecialMob;
import fathertoast.specialmobs.common.util.AttributeHelper;
import fathertoast.specialmobs.common.util.References;
import fathertoast.specialmobs.datagen.loot.LootTableBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SpecialMob
public class SpitfireSkeletonEntity extends _SpecialSkeletonEntity {
    
    //--------------- Static Special Mob Hooks ----------------
    
    @SpecialMob.SpeciesReference
    public static MobFamily.Species<SpitfireSkeletonEntity> SPECIES;
    
    @SpecialMob.BestiaryInfoSupplier
    public static BestiaryInfo bestiaryInfo( EntityType.Builder<LivingEntity> entityType ) {
        entityType.sized( 0.9F, 2.99F ).fireImmune();
        return new BestiaryInfo( 0xDC1A00, BestiaryInfo.BaseWeight.LOW );
        //TODO theme - fire
    }
    
    @SpecialMob.AttributeCreator
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AttributeHelper.of( _SpecialSkeletonEntity.createAttributes() )
                .addAttribute( Attributes.MAX_HEALTH, 20.0 )
                .addAttribute( Attributes.ATTACK_DAMAGE, 2.0 )
                .build();
    }
    
    @SpecialMob.LanguageProvider
    public static String[] getTranslations( String langKey ) {
        return References.translations( langKey, "Spitfire Skeleton",
                "", "", "", "", "", "" );//TODO
    }
    
    @SpecialMob.LootTableProvider
    public static void buildLootTable( LootTableBuilder loot ) {
        addBaseLoot( loot );
        loot.addCommonDrop( "common", Items.FIRE_CHARGE );
    }
    
    @SpecialMob.Factory
    public static EntityType.IFactory<SpitfireSkeletonEntity> getVariantFactory() { return SpitfireSkeletonEntity::new; }
    
    
    //--------------- Variant-Specific Implementations ----------------
    
    public SpitfireSkeletonEntity( EntityType<? extends _SpecialSkeletonEntity> entityType, World world ) {
        super( entityType, world );
        getSpecialData().setBaseScale( 1.5F );
        getSpecialData().setDamagedByWater( true );
        maxUpStep = 1.0F;
        xpReward += 2;
    }
    
    /** Override to change this entity's AI goals. */
    @Override
    protected void registerVariantGoals() {
        getSpecialData().rangedAttackDamage += 2.0F;
    }
    
    /** Override to change this entity's chance to spawn with a melee weapon. */
    @Override
    protected double getVariantMeleeChance() { return 0.0; }
    
    /** Override to apply effects when this entity hits a target with a melee attack. */
    @Override
    protected void onVariantAttack( Entity target ) {
        target.setSecondsOnFire( 10 );
    }
    
    /** Called to attack the target with a ranged attack. */
    @Override
    public void performRangedAttack( LivingEntity target, float damageMulti ) {
        if( !isSilent() ) level.levelEvent( null, 1018, blockPosition(), 0 );
        
        final float accelVariance = MathHelper.sqrt( distanceTo( target ) ) * 0.5F * getSpecialData().rangedAttackSpread;
        
        for( int i = 0; i < 3; i++ ) {
            final double dX = target.getX() - getX() + getRandom().nextGaussian() * accelVariance;
            final double dY = target.getEyeY() - getEyeY();
            final double dZ = target.getZ() - getZ() + getRandom().nextGaussian() * accelVariance;
            
            final SmallFireballEntity fireball = new SmallFireballEntity( level, this, dX, dY, dZ );
            fireball.setPos( fireball.getX(), getEyeY() - 0.1, fireball.getZ() );
            level.addFreshEntity( fireball );
        }
    }
    
    /** Sets this entity as a baby. */
    @Override
    public void setBaby( boolean value ) { }
    
    /** @return True if this entity is a baby. */
    @Override
    public boolean isBaby() { return false; }
    
    private static final ResourceLocation[] TEXTURES = {
            GET_TEXTURE_PATH( "fire" )
    };
    
    /** @return All default textures for this entity. */
    @Override
    public ResourceLocation[] getDefaultTextures() { return TEXTURES; }
}