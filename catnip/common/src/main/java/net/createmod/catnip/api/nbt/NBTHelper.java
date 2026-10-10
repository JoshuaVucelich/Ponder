package net.createmod.catnip.api.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

// TODO - Everything here needs to be rethought with how codecs exist now and should be used everywhere they can
@Deprecated(forRemoval = true)
public class NBTHelper {
	public static void putMarker(CompoundTag nbt, String marker) {
		nbt.store(marker, Unit.CODEC, Unit.INSTANCE);
	}

	// Backwards compatible with 1.20
	public static BlockPos readBlockPos(CompoundTag nbt, String key) {
		Optional<BlockPos> pos = nbt.read(key, BlockPos.CODEC);
		if (pos.isPresent())
			return pos.get();
		CompoundTag oldTag = nbt.getCompoundOrEmpty(key);
		return new BlockPos(
			oldTag.getIntOr("X", 0),
			oldTag.getIntOr("Y", 0),
			oldTag.getIntOr("Z", 0)
		);
	}

	public static <T> ListTag writeCompoundList(Iterable<T> list, Function<T, CompoundTag> serializer) {
		ListTag listNBT = new ListTag();
		list.forEach(t -> {
			CompoundTag apply = serializer.apply(t);
			if (apply == null)
				return;
			listNBT.add(apply);
		});
		return listNBT;
	}

	public static <T> List<T> readCompoundList(ListTag listNBT, Function<CompoundTag, T> deserializer) {
		List<T> list = new ArrayList<>(listNBT.size());
		listNBT.forEach(inbt -> list.add(deserializer.apply((CompoundTag) inbt)));
		return list;
	}

	public static void iterateCompoundList(ListTag listNBT, Consumer<CompoundTag> consumer) {
		listNBT.forEach(inbt -> consumer.accept((CompoundTag) inbt));
	}

	public static Tag getINBT(CompoundTag nbt, String id) {
		Tag inbt = nbt.get(id);
		if (inbt != null)
			return inbt;
		return new CompoundTag();
	}

	public static CompoundTag intToCompound(int i) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putInt("V", i);
		return compoundTag;
	}

	public static int intFromCompound(CompoundTag compoundTag) {
		return compoundTag.getIntOr("V", 0);
	}

	public static <T extends Enum<T>> void writeEnum(CompoundTag nbt, String key, T value) {
		nbt.putString(key, value.name());
	}

	public static <T extends Enum<T>> T readEnum(CompoundTag nbt, String key, Class<T> enumClass) {
		String name = nbt.getStringOr(key, "");
		if (name.isEmpty())
			return enumClass.getEnumConstants()[0];
		try {
			return Enum.valueOf(enumClass, name);
		} catch (IllegalArgumentException e) {
			return enumClass.getEnumConstants()[0];
		}
	}

	public static void writeResourceLocation(CompoundTag nbt, String key, Identifier id) {
		nbt.putString(key, id.toString());
	}

	public static Identifier readResourceLocation(CompoundTag nbt, String key) {
		return Identifier.parse(nbt.getStringOr(key, "minecraft:air"));
	}

	public static ListTag writeItemList(List<ItemStack> items, HolderLookup.Provider registries) {
		ListTag list = new ListTag();
		var ops = registries.createSerializationContext(NbtOps.INSTANCE);
		for (ItemStack stack : items) {
			ItemStack.OPTIONAL_CODEC.encodeStart(ops, stack)
				.result()
				.ifPresent(list::add);
		}
		return list;
	}

	public static List<ItemStack> readItemList(ListTag list, HolderLookup.Provider registries) {
		List<ItemStack> items = new ArrayList<>(list.size());
		var ops = registries.createSerializationContext(NbtOps.INSTANCE);
		for (int i = 0; i < list.size(); i++) {
			ItemStack.OPTIONAL_CODEC.parse(ops, list.get(i))
				.result()
				.filter(stack -> !stack.isEmpty())
				.ifPresent(items::add);
		}
		return items;
	}

}
