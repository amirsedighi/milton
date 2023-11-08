package io.meshk.milton.command.serde;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import io.meshk.milton.command.model.Permission;
import io.meshk.milton.command.model.User;

public class DataFactory implements DataSerializableFactory {
    public static final int FACTORY_ID = 1;

    @Override
    public IdentifiedDataSerializable create(int typeId) {
        if (typeId== User.CLASS_ID) {
            return new User();
        }
        if (typeId== Permission.CLASS_ID) {
            return new Permission();
        }
        return null;
    }
}
