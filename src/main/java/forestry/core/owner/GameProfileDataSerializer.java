package forestry.core.owner;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;

import com.mojang.authlib.GameProfile;

public class GameProfileDataSerializer implements IDataSerializer<Optional<GameProfile>> {
	public static final GameProfileDataSerializer INSTANCE = new GameProfileDataSerializer();

	public static void register() {
		DataSerializers.registerSerializer(INSTANCE);
	}

	private GameProfileDataSerializer() {

	}

	@Override
	public void write(PacketBuffer buf, Optional<GameProfile> value) {
		if (!value.isPresent()) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			GameProfile gameProfile = value.get();
			buf.writeUUID(gameProfile.getId());
			buf.writeUtf(gameProfile.getName());
		}
	}

	@Override
	public Optional<GameProfile> read(PacketBuffer buf) {
		if (buf.readBoolean()) {
			UUID uuid = buf.readUUID();
			String name = buf.readUtf(1024);
			GameProfile gameProfile = new GameProfile(uuid, name);
			return Optional.of(gameProfile);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public DataParameter<Optional<GameProfile>> createAccessor(int id) {
		return new DataParameter<>(id, this);
	}

	@Override
	public Optional<GameProfile> copy(Optional<GameProfile> value) {
		return value;
	}
}
