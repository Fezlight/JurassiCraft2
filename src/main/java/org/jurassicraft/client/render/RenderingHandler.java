package org.jurassicraft.client.render;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.model.animation.EntityAnimator;
import org.jurassicraft.client.model.animation.entity.BrachiosaurusAnimator;
import org.jurassicraft.client.model.animation.entity.CoelacanthAnimator;
import org.jurassicraft.client.model.animation.entity.DilophosaurusAnimator;
import org.jurassicraft.client.model.animation.entity.GallimimusAnimator;
import org.jurassicraft.client.model.animation.entity.MicroraptorAnimator;
import org.jurassicraft.client.model.animation.entity.MussaurusAnimator;
import org.jurassicraft.client.model.animation.entity.ParasaurolophusAnimator;
import org.jurassicraft.client.model.animation.entity.TriceratopsAnimator;
import org.jurassicraft.client.model.animation.entity.TyrannosaurusAnimator;
import org.jurassicraft.client.model.animation.entity.VelociraptorAnimator;
import org.jurassicraft.client.render.block.ActionFigureRenderer;
import org.jurassicraft.client.render.block.DNACombinatorHybridizerRenderer;
import org.jurassicraft.client.render.block.DNAExtractorRenderer;
import org.jurassicraft.client.render.block.DNASequencerRenderer;
import org.jurassicraft.client.render.block.DNASynthesizerRenderer;
import org.jurassicraft.client.render.block.ElectricFenceBaseRenderer;
import org.jurassicraft.client.render.block.ElectricFencePoleRenderer;
import org.jurassicraft.client.render.block.ElectricFenceWireRenderer;
import org.jurassicraft.client.render.block.EmbryoCalcificationMachineRenderer;
import org.jurassicraft.client.render.block.EmbryonicMachineRenderer;
import org.jurassicraft.client.render.block.FeederRenderer;
import org.jurassicraft.client.render.block.IncubatorRenderer;
import org.jurassicraft.client.render.entity.AttractionSignRenderer;
import org.jurassicraft.client.render.entity.DinosaurEggRenderer;
import org.jurassicraft.client.render.entity.GoatRenderer;
import org.jurassicraft.client.render.entity.HelicopterRenderer;
import org.jurassicraft.client.render.entity.JeepWranglerRenderer;
import org.jurassicraft.client.render.entity.MuralRenderer;
import org.jurassicraft.client.render.entity.PaddockSignRenderer;
import org.jurassicraft.client.render.entity.SeatRenderer;
import org.jurassicraft.client.render.entity.VenomRenderer;
import org.jurassicraft.client.render.entity.dinosaur.DinosaurRenderInfo;
import org.jurassicraft.server.api.Hybrid;
import org.jurassicraft.server.block.BlockHandler;
import org.jurassicraft.server.block.EncasedFossilBlock;
import org.jurassicraft.server.block.FossilBlock;
import org.jurassicraft.server.block.FossilizedTrackwayBlock;
import org.jurassicraft.server.block.NestFossilBlock;
import org.jurassicraft.server.block.entity.ActionFigureBlockEntity;
import org.jurassicraft.server.block.entity.DNACombinatorHybridizerBlockEntity;
import org.jurassicraft.server.block.entity.DNAExtractorBlockEntity;
import org.jurassicraft.server.block.entity.DNASequencerBlockEntity;
import org.jurassicraft.server.block.entity.DNASynthesizerBlockEntity;
import org.jurassicraft.server.block.entity.ElectricFenceBaseBlockEntity;
import org.jurassicraft.server.block.entity.ElectricFencePoleBlockEntity;
import org.jurassicraft.server.block.entity.ElectricFenceWireBlockEntity;
import org.jurassicraft.server.block.entity.EmbryoCalcificationMachineBlockEntity;
import org.jurassicraft.server.block.entity.EmbryonicMachineBlockEntity;
import org.jurassicraft.server.block.entity.FeederBlockEntity;
import org.jurassicraft.server.block.entity.IncubatorBlockEntity;
import org.jurassicraft.server.block.fence.ElectricFenceBaseBlock;
import org.jurassicraft.server.block.fence.ElectricFencePoleBlock;
import org.jurassicraft.server.block.fence.ElectricFenceWireBlock;
import org.jurassicraft.server.block.plant.AncientCoralBlock;
import org.jurassicraft.server.block.tree.AncientLeavesBlock;
import org.jurassicraft.server.block.tree.TreeType;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.entity.GoatEntity;
import org.jurassicraft.server.entity.VenomEntity;
import org.jurassicraft.server.entity.item.AttractionSignEntity;
import org.jurassicraft.server.entity.item.DinosaurEggEntity;
import org.jurassicraft.server.entity.item.MuralEntity;
import org.jurassicraft.server.entity.item.PaddockSignEntity;
import org.jurassicraft.server.entity.vehicle.HelicopterBaseEntity;
import org.jurassicraft.server.entity.vehicle.JeepWranglerEntity;
import org.jurassicraft.server.entity.vehicle.modules.SeatEntity;
import org.jurassicraft.server.item.DinosaurSpawnEggItem;
import org.jurassicraft.server.item.FossilItem;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.plant.Plant;
import org.jurassicraft.server.plant.PlantHandler;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@SideOnly(Side.CLIENT)
public enum RenderingHandler {
    INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();
    private Map<Dinosaur, DinosaurRenderInfo> renderInfos = Maps.newHashMap();
    private ItemModelMesher modelMesher;

    public void preInit() {
        for (Dinosaur dino : EntityHandler.getDinosaurs().values()) {
            String dinoName = dino.getName().toLowerCase(Locale.ENGLISH).replaceAll(" ", "_");

            if (!(dino instanceof Hybrid)) {
                for (Map.Entry<String, FossilItem> entry : ItemHandler.FOSSILS.entrySet()) {
                    List<Dinosaur> dinosaursForType = FossilItem.fossilDinosaurs.get(entry.getKey());

                    if (dinosaursForType.contains(dino)) {
                        ModelBakery.registerItemVariants(entry.getValue(), new ResourceLocation("jurassicraft:bones/" + dinoName + "_" + entry.getKey()));
                    }
                }
            }

            for (Map.Entry<String, FossilItem> entry : ItemHandler.FRESH_FOSSILS.entrySet()) {
                List<Dinosaur> dinosaursForType = FossilItem.fossilDinosaurs.get(entry.getKey());

                if (dinosaursForType.contains(dino)) {
                    ModelBakery.registerItemVariants(entry.getValue(), new ResourceLocation("jurassicraft:fresh_bones/" + dinoName + "_" + entry.getKey()));
                }
            }

            ModelBakery.registerItemVariants(ItemHandler.DNA, new ResourceLocation("jurassicraft:dna/dna_" + dinoName));

            if (!dino.isMammal() && !dino.isMarineCreature()) {
                ModelBakery.registerItemVariants(ItemHandler.EGG, new ResourceLocation("jurassicraft:egg/egg_" + dinoName));
                ModelBakery.registerItemVariants(ItemHandler.HATCHED_EGG, new ResourceLocation("jurassicraft:hatched_egg/egg_" + dinoName));
            }

            ModelBakery.registerItemVariants(ItemHandler.DINOSAUR_MEAT, new ResourceLocation("jurassicraft:meat/meat_" + dinoName));
            ModelBakery.registerItemVariants(ItemHandler.DINOSAUR_STEAK, new ResourceLocation("jurassicraft:meat/steak_" + dinoName));
            ModelBakery.registerItemVariants(ItemHandler.SOFT_TISSUE, new ResourceLocation("jurassicraft:soft_tissue/soft_tissue_" + dinoName));
            ModelBakery.registerItemVariants(ItemHandler.SYRINGE, new ResourceLocation("jurassicraft:syringe/syringe_" + dinoName));
            ModelBakery.registerItemVariants(ItemHandler.ACTION_FIGURE, new ResourceLocation("jurassicraft:action_figure/action_figure_" + dinoName));
        }

        for (FossilizedTrackwayBlock.TrackwayType trackwayType : FossilizedTrackwayBlock.TrackwayType.values()) {
            ModelBakery.registerItemVariants(Item.getItemFromBlock(BlockHandler.FOSSILIZED_TRACKWAY), new ResourceLocation("jurassicraft:fossilized_trackway_" + trackwayType.getName()));
        }

        for (NestFossilBlock.Variant variant : NestFossilBlock.Variant.values()) {
            ModelBakery.registerItemVariants(Item.getItemFromBlock(BlockHandler.NEST_FOSSIL), new ResourceLocation("jurassicraft:nest_fossil_" + (variant.ordinal() + 1)));
            ModelBakery.registerItemVariants(ItemHandler.FOSSILIZED_EGG, new ResourceLocation("jurassicraft:fossilized_egg_" + (variant.ordinal() + 1)));
        }

        for (Plant plant : PlantHandler.getPrehistoricPlantsAndTrees()) {
            String name = plant.getName().toLowerCase(Locale.ENGLISH).replaceAll(" ", "_");

            ModelBakery.registerItemVariants(ItemHandler.PLANT_DNA, new ResourceLocation("jurassicraft:dna/plants/dna_" + name));
            ModelBakery.registerItemVariants(ItemHandler.PLANT_SOFT_TISSUE, new ResourceLocation("jurassicraft:soft_tissue/plants/soft_tissue_" + name));
        }

//        for (EnumDyeColor color : EnumDyeColor.values()) {
//            ModelBakery.registerItemVariants(Item.getItemFromBlock(BlockHandler.CULTIVATOR_BOTTOM), new ResourceLocation("jurassicraft:cultivate/cultivate_bottom_" + color.getName().toLowerCase(Locale.ENGLISH)));
//        }

        for (AttractionSignEntity.AttractionSignType type : AttractionSignEntity.AttractionSignType.values()) {
            ModelBakery.registerItemVariants(ItemHandler.ATTRACTION_SIGN, new ResourceLocation("jurassicraft:attraction_sign_" + type.name().toLowerCase(Locale.ENGLISH)));
        }

        ModelBakery.registerItemVariants(ItemHandler.AMBER, new ResourceLocation("jurassicraft:amber_aphid"), new ResourceLocation("jurassicraft:amber_mosquito"));

        for (TreeType type : TreeType.values()) {
            ModelLoader.setCustomStateMapper(BlockHandler.ANCIENT_FENCE_GATES.get(type), (new StateMap.Builder()).ignore(new IProperty[] { BlockFenceGate.POWERED }).build());
            ModelLoader.setCustomStateMapper(BlockHandler.ANCIENT_DOORS.get(type), (new StateMap.Builder()).ignore(new IProperty[] { BlockDoor.POWERED }).build());
        }

        ModelLoader.setCustomStateMapper(BlockHandler.LOW_SECURITY_FENCE_BASE, (new StateMap.Builder().ignore(new IProperty[] { ElectricFenceBaseBlock.NORTH, ElectricFenceBaseBlock.SOUTH, ElectricFenceBaseBlock.WEST, ElectricFenceBaseBlock.EAST, ElectricFenceBaseBlock.POLE })).build());
        ModelLoader.setCustomStateMapper(BlockHandler.LOW_SECURITY_FENCE_POLE, (new StateMap.Builder().ignore(new IProperty[] { ElectricFencePoleBlock.NORTH, ElectricFencePoleBlock.SOUTH, ElectricFencePoleBlock.WEST, ElectricFencePoleBlock.EAST, ElectricFencePoleBlock.POWERED })).build());
        ModelLoader.setCustomStateMapper(BlockHandler.LOW_SECURITY_FENCE_WIRE, (new StateMap.Builder().ignore(new IProperty[] { ElectricFenceWireBlock.NORTH, ElectricFenceWireBlock.SOUTH, ElectricFenceWireBlock.WEST, ElectricFenceWireBlock.EAST, ElectricFenceWireBlock.UP_DIRECTION })).build());

        ModelLoader.setCustomStateMapper(BlockHandler.ENALLHELIA, (new StateMap.Builder().ignore(new IProperty[] { AncientCoralBlock.LEVEL })).build());
        ModelLoader.setCustomStateMapper(BlockHandler.AULOPORA, (new StateMap.Builder().ignore(new IProperty[] { AncientCoralBlock.LEVEL })).build());
        ModelLoader.setCustomStateMapper(BlockHandler.CLADOCHONUS, (new StateMap.Builder().ignore(new IProperty[] { AncientCoralBlock.LEVEL})).build());

        this.registerRenderInfo(EntityHandler.BRACHIOSAURUS, new BrachiosaurusAnimator(), 1.5F);
        this.registerRenderInfo(EntityHandler.COELACANTH, new CoelacanthAnimator(), 0.0F);
        this.registerRenderInfo(EntityHandler.DILOPHOSAURUS, new DilophosaurusAnimator(), 0.65F);
        this.registerRenderInfo(EntityHandler.GALLIMIMUS, new GallimimusAnimator(), 0.65F);
        this.registerRenderInfo(EntityHandler.PARASAUROLOPHUS, new ParasaurolophusAnimator(), 0.65F);
        this.registerRenderInfo(EntityHandler.MICRORAPTOR, new MicroraptorAnimator(), 0.45F);
        this.registerRenderInfo(EntityHandler.MUSSAURUS, new MussaurusAnimator(), 0.8F);
        this.registerRenderInfo(EntityHandler.TRICERATOPS, new TriceratopsAnimator(), 0.65F);
        this.registerRenderInfo(EntityHandler.TYRANNOSAURUS, new TyrannosaurusAnimator(), 0.65F);
        this.registerRenderInfo(EntityHandler.VELOCIRAPTOR, new VelociraptorAnimator(), 0.45F);

        RenderingRegistry.registerEntityRenderingHandler(PaddockSignEntity.class, new PaddockSignRenderer());
        RenderingRegistry.registerEntityRenderingHandler(AttractionSignEntity.class, new AttractionSignRenderer());
        RenderingRegistry.registerEntityRenderingHandler(HelicopterBaseEntity.class, new HelicopterRenderer());
        RenderingRegistry.registerEntityRenderingHandler(DinosaurEggEntity.class, new DinosaurEggRenderer());
        RenderingRegistry.registerEntityRenderingHandler(VenomEntity.class, new VenomRenderer());
        RenderingRegistry.registerEntityRenderingHandler(JeepWranglerEntity.class, new JeepWranglerRenderer());
        RenderingRegistry.registerEntityRenderingHandler(SeatEntity.class, new SeatRenderer());
        RenderingRegistry.registerEntityRenderingHandler(MuralEntity.class, MuralRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GoatEntity.class, GoatRenderer::new);
    }

    public void init() {
        this.modelMesher = this.mc.getRenderItem().getItemModelMesher();

        int i = 0;

        for (EncasedFossilBlock fossil : BlockHandler.ENCASED_FOSSILS) {
            this.registerBlockRenderer(fossil, "encased_fossil_" + i, "inventory");

            i++;
        }

        i = 0;

        for (FossilBlock fossil : BlockHandler.FOSSILS) {
            this.registerBlockRenderer(fossil, "fossil_block_" + i, "inventory");

            i++;
        }

        this.registerBlockRenderer(BlockHandler.PLANT_FOSSIL, "plant_fossil_block", "inventory");
        this.registerBlockRenderer(BlockHandler.FOSSILIZED_TRACKWAY, "fossilized_trackway", "inventory");

        for (TreeType type : TreeType.values()) {
            String name = type.name().toLowerCase(Locale.ENGLISH);
            this.registerBlockRenderer(BlockHandler.ANCIENT_LEAVES.get(type), name + "_leaves", "inventory");
            this.registerBlockRenderer(BlockHandler.ANCIENT_SAPLINGS.get(type), name + "_sapling", "inventory");
            this.registerBlockRenderer(BlockHandler.ANCIENT_PLANKS.get(type), name + "_planks", "inventory");
            this.registerBlockRenderer(BlockHandler.ANCIENT_LOGS.get(type), name + "_log", "inventory");
            this.registerBlockRenderer(BlockHandler.ANCIENT_STAIRS.get(type), name + "_stairs", "inventory");
            this.registerBlockRenderer(BlockHandler.ANCIENT_SLABS.get(type), name + "_slab", "inventory");
            this.registerBlockRenderer(BlockHandler.ANCIENT_DOUBLE_SLABS.get(type), name + "_double_slab", "inventory");
            this.registerBlockRenderer(BlockHandler.ANCIENT_FENCES.get(type), name + "_fence", "inventory");
            this.registerBlockRenderer(BlockHandler.ANCIENT_FENCE_GATES.get(type), name + "_fence_gate", "inventory");
            this.registerBlockRenderer(BlockHandler.PETRIFIED_LOGS.get(type), name + "_log_petrified", "inventory");
        }

        for (EnumDyeColor color : EnumDyeColor.values()) {
            this.registerBlockRenderer(BlockHandler.CULTIVATOR_BOTTOM, color.ordinal(), "cultivate/cultivate_bottom_" + color.getName().toLowerCase(Locale.ENGLISH), "inventory");
        }

        this.registerBlockRenderer(BlockHandler.SCALY_TREE_FERN, "scaly_tree_fern", "inventory");
        this.registerBlockRenderer(BlockHandler.SMALL_ROYAL_FERN, "small_royal_fern", "inventory");
        this.registerBlockRenderer(BlockHandler.SMALL_CHAIN_FERN, "small_chain_fern", "inventory");
        this.registerBlockRenderer(BlockHandler.SMALL_CYCAD, "small_cycad", "inventory");
        this.registerBlockRenderer(BlockHandler.CYCADEOIDEA, "bennettitalean_cycadeoidea", "inventory");
        this.registerBlockRenderer(BlockHandler.CRY_PANSY, "cry_pansy", "inventory");
        this.registerBlockRenderer(BlockHandler.ZAMITES, "cycad_zamites", "inventory");
        this.registerBlockRenderer(BlockHandler.DICKSONIA, "dicksonia", "inventory");
        this.registerBlockRenderer(BlockHandler.WOOLLY_STALKED_BEGONIA, "woolly_stalked_begonia", "inventory");
        this.registerBlockRenderer(BlockHandler.LARGESTIPULE_LEATHER_ROOT, "largestipule_leather_root", "inventory");
        this.registerBlockRenderer(BlockHandler.RHACOPHYTON, "rhacophyton", "inventory");
        this.registerBlockRenderer(BlockHandler.GRAMINIDITES_BAMBUSOIDES, "graminidites_bambusoides", "inventory");
        this.registerBlockRenderer(BlockHandler.ENALLHELIA, "enallhelia", "inventory");
        this.registerBlockRenderer(BlockHandler.AULOPORA, "aulopora", "inventory");
        this.registerBlockRenderer(BlockHandler.CLADOCHONUS, "cladochonus", "inventory");

        this.registerBlockRenderer(BlockHandler.REINFORCED_STONE, "reinforced_stone", "inventory");
        this.registerBlockRenderer(BlockHandler.REINFORCED_BRICKS, "reinforced_bricks", "inventory");

        this.registerBlockRenderer(BlockHandler.CULTIVATOR_BOTTOM, "cultivate_bottom", "inventory");
        this.registerBlockRenderer(BlockHandler.CULTIVATOR_TOP, "cultivate_bottom", "inventory");

        this.registerBlockRenderer(BlockHandler.AMBER_ORE, "amber_ore", "inventory");
        this.registerBlockRenderer(BlockHandler.ICE_SHARD, "ice_shard", "inventory");
        this.registerBlockRenderer(BlockHandler.CLEANING_STATION, "cleaning_station", "inventory");
        this.registerBlockRenderer(BlockHandler.FOSSIL_GRINDER, "fossil_grinder", "inventory");
        this.registerBlockRenderer(BlockHandler.DNA_SEQUENCER, "dna_sequencer", "inventory");
        this.registerBlockRenderer(BlockHandler.DNA_COMBINATOR_HYBRIDIZER, "dna_combinator_hybridizer", "inventory");
        this.registerBlockRenderer(BlockHandler.DNA_SYNTHESIZER, "dna_synthesizer", "inventory");
        this.registerBlockRenderer(BlockHandler.EMBRYONIC_MACHINE, "embryonic_machine", "inventory");
        this.registerBlockRenderer(BlockHandler.EMBRYO_CALCIFICATION_MACHINE, "embryo_calcification_machine", "inventory");
        this.registerBlockRenderer(BlockHandler.INCUBATOR, "incubator", "inventory");
        this.registerBlockRenderer(BlockHandler.DNA_EXTRACTOR, "dna_extractor", "inventory");
        this.registerBlockRenderer(BlockHandler.FEEDER, "feeder", "inventory");
        this.registerBlockRenderer(BlockHandler.GYPSUM_STONE, "gypsum_stone", "inventory");
        this.registerBlockRenderer(BlockHandler.GYPSUM_COBBLESTONE, "gypsum_cobblestone", "inventory");
        this.registerBlockRenderer(BlockHandler.GYPSUM_BRICKS, "gypsum_bricks", "inventory");
        this.registerBlockRenderer(BlockHandler.ACTION_FIGURE, "action_figure_block", "inventory");

        this.registerBlockRenderer(BlockHandler.MOSS, "moss", "inventory");
        this.registerBlockRenderer(BlockHandler.CLEAR_GLASS, "clear_glass", "inventory");

        this.registerBlockRenderer(BlockHandler.WILD_ONION, "wild_onion_plant", "inventory");
        this.registerBlockRenderer(BlockHandler.GRACILARIA, "graciliaria_seaweed", "inventory");
        this.registerBlockRenderer(BlockHandler.PEAT, "peat", "inventory");
        this.registerBlockRenderer(BlockHandler.PEAT_MOSS, "peat_moss", "inventory");
        this.registerBlockRenderer(BlockHandler.DICROIDIUM_ZUBERI, "dicroidium_zuberi", "inventory");
        this.registerBlockRenderer(BlockHandler.WEST_INDIAN_LILAC, "west_indian_lilac", "inventory");
        this.registerBlockRenderer(BlockHandler.DICTYOPHYLLUM, "dictyophyllum", "inventory");
        this.registerBlockRenderer(BlockHandler.SERENNA_VERIFORMANS, "serenna_veriformans", "inventory");
        this.registerBlockRenderer(BlockHandler.LADINIA_SIMPLEX, "ladinia_simplex", "inventory");
        this.registerBlockRenderer(BlockHandler.ORONTIUM_MACKII, "orontium_mackii", "inventory");
        this.registerBlockRenderer(BlockHandler.UMALTOLEPIS, "umaltolepis", "inventory");
        this.registerBlockRenderer(BlockHandler.LIRIODENDRITES, "liriodendrites", "inventory");
        this.registerBlockRenderer(BlockHandler.RAPHAELIA, "raphaelia", "inventory");
        this.registerBlockRenderer(BlockHandler.ENCEPHALARTOS, "encephalartos", "inventory");

        for (FossilizedTrackwayBlock.TrackwayType trackwayType : FossilizedTrackwayBlock.TrackwayType.values()) {
            this.registerBlockRenderer(BlockHandler.FOSSILIZED_TRACKWAY, trackwayType.ordinal(), "fossilized_trackway_" + trackwayType.getName(), "inventory");
        }

        for (NestFossilBlock.Variant variant : NestFossilBlock.Variant.values()) {
            this.registerBlockRenderer(BlockHandler.NEST_FOSSIL, variant.ordinal(), "nest_fossil_" + (variant.ordinal() + 1), "inventory");
            this.registerBlockRenderer(BlockHandler.ENCASED_NEST_FOSSIL, variant.ordinal(), "encased_nest_fossil", "inventory");
        }

        this.registerBlockRenderer(BlockHandler.PALEO_BALE_CYCADEOIDEA, "paleo_bale_cycadeoidea", "inventory");
        this.registerBlockRenderer(BlockHandler.PALEO_BALE_CYCAD, "paleo_bale_cycad", "inventory");
        this.registerBlockRenderer(BlockHandler.PALEO_BALE_FERN, "paleo_bale_fern", "inventory");
        this.registerBlockRenderer(BlockHandler.PALEO_BALE_LEAVES, "paleo_bale_leaves", "inventory");
        this.registerBlockRenderer(BlockHandler.PALEO_BALE_OTHER, "paleo_bale_other", "inventory");

        BlockColors blockColors = this.mc.getBlockColors();
        blockColors.registerBlockColorHandler((state, access, pos, tintIndex) -> pos != null ? BiomeColorHelper.getGrassColorAtPos(access, pos) : 0xFFFFFF, BlockHandler.MOSS);

        for (Map.Entry<TreeType, AncientLeavesBlock> entry : BlockHandler.ANCIENT_LEAVES.entrySet()) {
            blockColors.registerBlockColorHandler((state, access, pos, tintIndex) -> pos == null ? ColorizerFoliage.getFoliageColorBasic() : BiomeColorHelper.getFoliageColorAtPos(access, pos), entry.getValue());
        }

        blockColors.registerBlockColorHandler((state, access, pos, tintIndex) -> pos == null ? ColorizerFoliage.getFoliageColorBasic() : BiomeColorHelper.getFoliageColorAtPos(access, pos), BlockHandler.MOSS);

        ItemColors itemColors = this.mc.getItemColors();

        for (Map.Entry<TreeType, AncientLeavesBlock> entry : BlockHandler.ANCIENT_LEAVES.entrySet()) {
            itemColors.registerItemColorHandler((stack, tintIndex) -> ColorizerFoliage.getFoliageColorBasic(), entry.getValue());
        }

        this.registerBlockRenderer(BlockHandler.AJUGINUCULA_SMITHII);
        this.registerBlockRenderer(BlockHandler.BUG_CRATE);

        this.registerBlockRenderer(BlockHandler.PLANKTON_SWARM);
        this.registerBlockRenderer(BlockHandler.KRILL_SWARM);

        this.registerBlockRenderer(BlockHandler.LOW_SECURITY_FENCE_POLE);
        this.registerBlockRenderer(BlockHandler.LOW_SECURITY_FENCE_BASE);
        this.registerBlockRenderer(BlockHandler.LOW_SECURITY_FENCE_WIRE);

        this.registerBlockRenderer(BlockHandler.WILD_POTATO_PLANT);
        this.registerBlockRenderer(BlockHandler.TEMPSKYA);
        this.registerBlockRenderer(BlockHandler.CINNAMON_FERN);
        this.registerBlockRenderer(BlockHandler.BRISTLE_FERN);
    }

    public void postInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(DNAExtractorBlockEntity.class, new DNAExtractorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ActionFigureBlockEntity.class, new ActionFigureRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DNASequencerBlockEntity.class, new DNASequencerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(EmbryoCalcificationMachineBlockEntity.class, new EmbryoCalcificationMachineRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DNACombinatorHybridizerBlockEntity.class, new DNACombinatorHybridizerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(EmbryonicMachineBlockEntity.class, new EmbryonicMachineRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DNASynthesizerBlockEntity.class, new DNASynthesizerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(IncubatorBlockEntity.class, new IncubatorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(FeederBlockEntity.class, new FeederRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ElectricFencePoleBlockEntity.class, new ElectricFencePoleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ElectricFenceBaseBlockEntity.class, new ElectricFenceBaseRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ElectricFenceWireBlockEntity.class, new ElectricFenceWireRenderer());

        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockHandler.LOW_SECURITY_FENCE_POLE), 0, ElectricFencePoleBlockEntity.class);
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockHandler.LOW_SECURITY_FENCE_BASE), 0, ElectricFenceBaseBlockEntity.class);

        this.registerItemRenderer(ItemHandler.TRACKER);
        this.registerItemRenderer(ItemHandler.PLANT_CELLS_PETRI_DISH);
        this.registerItemRenderer(ItemHandler.PLANT_CELLS);
        this.registerItemRenderer(ItemHandler.PLANT_CALLUS);
        this.registerItemRenderer(ItemHandler.GROWTH_SERUM);
        this.registerItemRenderer(ItemHandler.IRON_ROD);
        this.registerItemRenderer(ItemHandler.IRON_BLADES);
        this.registerItemRenderer(ItemHandler.PETRI_DISH);
        this.registerItemRenderer(ItemHandler.PETRI_DISH_AGAR);
        this.registerItemRenderer(ItemHandler.AMBER);
        this.registerItemRenderer(ItemHandler.PLASTER_AND_BANDAGE);
        this.registerItemRenderer(ItemHandler.SPAWN_EGG);
        this.registerItemRenderer(ItemHandler.PADDOCK_SIGN);

        for (AttractionSignEntity.AttractionSignType type : AttractionSignEntity.AttractionSignType.values()) {
            this.registerItemRenderer(ItemHandler.ATTRACTION_SIGN, type.ordinal(), "attraction_sign_" + type.name().toLowerCase(Locale.ENGLISH), "inventory");
        }

        this.registerItemRenderer(ItemHandler.EMPTY_TEST_TUBE);
        this.registerItemRenderer(ItemHandler.EMPTY_SYRINGE);
        this.registerItemRenderer(ItemHandler.STORAGE_DISC);
        this.registerItemRenderer(ItemHandler.DISC_DRIVE, "disc_reader", "inventory");
        this.registerItemRenderer(ItemHandler.LASER);
        this.registerItemRenderer(ItemHandler.DNA_NUCLEOTIDES, "dna_base_material", "inventory");
        this.registerItemRenderer(ItemHandler.SEA_LAMPREY);

        this.registerItemRenderer(ItemHandler.AMBER, 0, "amber_mosquito", "inventory");
        this.registerItemRenderer(ItemHandler.AMBER, 1, "amber_aphid", "inventory");

        this.registerItemRenderer(ItemHandler.HELICOPTER, "helicopter_spawner", "inventory");

        this.registerItemRenderer(ItemHandler.JURASSICRAFT_THEME_DISC, "disc_jurassicraft_theme", "inventory");
        this.registerItemRenderer(ItemHandler.DONT_MOVE_A_MUSCLE_DISC, "disc_dont_move_a_muscle", "inventory");
        this.registerItemRenderer(ItemHandler.TROODONS_AND_RAPTORS_DISC, "disc_troodons_and_raptors", "inventory");

        this.registerItemRenderer(ItemHandler.AMBER_KEYCHAIN, "amber_keychain", "inventory");
        this.registerItemRenderer(ItemHandler.AMBER_CANE, "amber_cane", "inventory");
        this.registerItemRenderer(ItemHandler.MR_DNA_KEYCHAIN, "mr_dna_keychain", "inventory");

        this.registerItemRenderer(ItemHandler.DINO_SCANNER, "dino_scanner", "inventory");

        this.registerItemRenderer(ItemHandler.BASIC_CIRCUIT, "basic_circuit", "inventory");
        this.registerItemRenderer(ItemHandler.ADVANCED_CIRCUIT, "advanced_circuit", "inventory");
        this.registerItemRenderer(ItemHandler.IRON_NUGGET, "iron_nugget", "inventory");

        this.registerItemRenderer(ItemHandler.GYPSUM_POWDER, "gypsum_powder", "inventory");

        this.registerItemRenderer(ItemHandler.AJUGINUCULA_SMITHII_SEEDS, "ajuginucula_smithii_seeds", "inventory");
        this.registerItemRenderer(ItemHandler.AJUGINUCULA_SMITHII_LEAVES, "ajuginucula_smithii_leaves", "inventory");
        this.registerItemRenderer(ItemHandler.AJUGINUCULA_SMITHII_OIL, "ajuginucula_smithii_oil", "inventory");

        this.registerItemRenderer(ItemHandler.WILD_ONION, "wild_onion", "inventory");
        this.registerItemRenderer(ItemHandler.GRACILARIA, "gracilaria", "inventory");
        this.registerItemRenderer(ItemHandler.LIQUID_AGAR, "liquid_agar", "inventory");

        this.registerItemRenderer(ItemHandler.PLANT_FOSSIL, "plant_fossil", "inventory");
        this.registerItemRenderer(ItemHandler.TWIG_FOSSIL, "twig_fossil", "inventory");

        this.registerItemRenderer(ItemHandler.KEYBOARD, "keyboard", "inventory");
        this.registerItemRenderer(ItemHandler.COMPUTER_SCREEN, "computer_screen", "inventory");
        this.registerItemRenderer(ItemHandler.DNA_ANALYZER, "dna_analyzer", "inventory");

        this.registerItemRenderer(ItemHandler.CHILEAN_SEA_BASS, "chilean_sea_bass", "inventory");
        this.registerItemRenderer(ItemHandler.FIELD_GUIDE, "field_guide", "inventory");

        this.registerItemRenderer(ItemHandler.CAR_CHASSIS, "car_chassis", "inventory");
        this.registerItemRenderer(ItemHandler.CAR_ENGINE_SYSTEM, "car_engine_system", "inventory");
        this.registerItemRenderer(ItemHandler.CAR_SEATS, "car_seats", "inventory");
        this.registerItemRenderer(ItemHandler.CAR_TIRE, "car_tire", "inventory");
        this.registerItemRenderer(ItemHandler.CAR_WINDSCREEN, "car_windscreen", "inventory");
        this.registerItemRenderer(ItemHandler.UNFINISHED_CAR, "unfinished_car", "inventory");
        this.registerItemRenderer(ItemHandler.JEEP_WRANGLER, "jeep_wrangler", "inventory");

        this.registerItemRenderer(ItemHandler.MURAL, "mural", "inventory");

        for (Dinosaur dinosaur : EntityHandler.getDinosaurs().values()) {
            int meta = EntityHandler.getDinosaurId(dinosaur);

            String formattedName = dinosaur.getName().toLowerCase(Locale.ENGLISH).replaceAll(" ", "_");

            for (Map.Entry<String, FossilItem> entry : ItemHandler.FOSSILS.entrySet()) {
                List<Dinosaur> dinosaursForType = FossilItem.fossilDinosaurs.get(entry.getKey());
                if (dinosaursForType.contains(dinosaur)) {
                    this.registerItemRenderer(entry.getValue(), meta, "bones/" + formattedName + "_" + entry.getKey(), "inventory");
                }
            }

            for (Map.Entry<String, FossilItem> entry : ItemHandler.FRESH_FOSSILS.entrySet()) {
                List<Dinosaur> dinosaursForType = FossilItem.fossilDinosaurs.get(entry.getKey());
                if (dinosaursForType.contains(dinosaur)) {
                    this.registerItemRenderer(entry.getValue(), meta, "fresh_bones/" + formattedName + "_" + entry.getKey(), "inventory");
                }
            }

            this.registerItemRenderer(ItemHandler.DNA, meta, "dna/dna_" + formattedName, "inventory");
            this.registerItemRenderer(ItemHandler.DINOSAUR_MEAT, meta, "meat/meat_" + formattedName, "inventory");
            this.registerItemRenderer(ItemHandler.DINOSAUR_STEAK, meta, "meat/steak_" + formattedName, "inventory");
            this.registerItemRenderer(ItemHandler.SOFT_TISSUE, meta, "soft_tissue/soft_tissue_" + formattedName, "inventory");
            this.registerItemRenderer(ItemHandler.SYRINGE, meta, "syringe/syringe_" + formattedName, "inventory");
            this.registerItemRenderer(ItemHandler.ACTION_FIGURE, meta, "action_figure/action_figure_" + formattedName, "inventory");

            if (!dinosaur.isMarineCreature() && !dinosaur.isMammal()) {
                this.registerItemRenderer(ItemHandler.EGG, meta, "egg/egg_" + formattedName, "inventory");
                this.registerItemRenderer(ItemHandler.HATCHED_EGG, meta, "hatched_egg/egg_" + formattedName, "inventory");
            }
        }

        for (Plant plant : PlantHandler.getPrehistoricPlantsAndTrees()) {
            int meta = PlantHandler.getPlantId(plant);

            String name = plant.getName().toLowerCase(Locale.ENGLISH).replaceAll(" ", "_");

            this.registerItemRenderer(ItemHandler.PLANT_DNA, meta, "dna/plants/dna_" + name, "inventory");
            this.registerItemRenderer(ItemHandler.PLANT_SOFT_TISSUE, meta, "soft_tissue/plants/soft_tissue_" + name, "inventory");
        }

        for (NestFossilBlock.Variant variant : NestFossilBlock.Variant.values()) {
            this.registerItemRenderer(ItemHandler.FOSSILIZED_EGG, variant.ordinal(), "fossilized_egg_" + (variant.ordinal() + 1), "inventory");
        }

        for (TreeType type : TreeType.values()) {
            String name = type.name().toLowerCase(Locale.ENGLISH);
            this.registerItemRenderer(ItemHandler.ANCIENT_DOORS.get(type), name + "_door_item", "inventory");
        }

        this.registerItemRenderer(ItemHandler.PHOENIX_SEEDS);
        this.registerItemRenderer(ItemHandler.PHOENIX_FRUIT);

        this.registerItemRenderer(ItemHandler.CRICKETS);
        this.registerItemRenderer(ItemHandler.COCKROACHES);
        this.registerItemRenderer(ItemHandler.MEALWORM_BEETLES);

        this.registerItemRenderer(ItemHandler.FINE_NET);
        this.registerItemRenderer(ItemHandler.PLANKTON);
        this.registerItemRenderer(ItemHandler.KRILL);

        this.registerItemRenderer(ItemHandler.WILD_POTATO_SEEDS);
        this.registerItemRenderer(ItemHandler.WILD_POTATO);
        this.registerItemRenderer(ItemHandler.WILD_POTATO_COOKED);

        this.registerItemRenderer(ItemHandler.GOAT_RAW);
        this.registerItemRenderer(ItemHandler.GOAT_COOKED);

        ItemColors itemColors = this.mc.getItemColors();
        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            DinosaurSpawnEggItem item = (DinosaurSpawnEggItem) stack.getItem();
            Dinosaur dino = item.getDinosaur(stack);
            if (dino != null) {
                int mode = item.getMode(stack);
                if (mode == 0) {
                    mode = JurassiCraft.timerTicks % 64 > 32 ? 1 : 2;
                }
                if (mode == 1) {
                    return tintIndex == 0 ? dino.getEggPrimaryColorMale() : dino.getEggSecondaryColorMale();
                } else {
                    return tintIndex == 0 ? dino.getEggPrimaryColorFemale() : dino.getEggSecondaryColorFemale();
                }
            }
            return 0xFFFFFF;
        }, ItemHandler.SPAWN_EGG);
    }

    public void registerItemRenderer(Item item) {
        this.registerItemRenderer(item, item.getUnlocalizedName().substring("item.".length()), "inventory");
    }

    public void registerBlockRenderer(Block block) {
        this.registerBlockRenderer(block, block.getUnlocalizedName().substring("item.".length()), "inventory");
    }

    public void registerItemRenderer(Item item, final String path, final String type) {
        this.modelMesher.register(item, stack -> new ModelResourceLocation(JurassiCraft.MODID + ":" + path, type));
    }

    public void registerItemRenderer(Item item, int meta, String path, String type) {
        this.modelMesher.register(item, meta, new ModelResourceLocation(JurassiCraft.MODID + ":" + path, type));
    }

    public void registerBlockRenderer(Block block, int meta, String path, String type) {
        this.modelMesher.register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(JurassiCraft.MODID + ":" + path, type));
    }

    public void registerBlockRenderer(Block block, final String path, final String type) {
        this.modelMesher.register(Item.getItemFromBlock(block), stack -> new ModelResourceLocation(JurassiCraft.MODID + ":" + path, type));
    }

    private void registerRenderInfo(Dinosaur dinosaur, EntityAnimator<?> animator, float shadowSize) {
        this.registerRenderInfo(new DinosaurRenderInfo(dinosaur, animator, shadowSize));
    }

    private void registerRenderInfo(DinosaurRenderInfo renderDef) {
        this.renderInfos.put(renderDef.getDinosaur(), renderDef);
        RenderingRegistry.registerEntityRenderingHandler(renderDef.getDinosaur().getDinosaurClass(), renderDef);
    }

    public DinosaurRenderInfo getRenderInfo(Dinosaur dino) {
        return this.renderInfos.get(dino);
    }
}
