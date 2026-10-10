package net.createmod.catnip.api.placement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlacementHelpers {
	private static final List<IPlacementHelper> registry = new ArrayList<>();
	private static final List<IPlacementHelper> registryView = Collections.unmodifiableList(registry);

	public static int register(IPlacementHelper helper) {
		registry.add(helper);
		return registry.size() - 1;
	}

	public static IPlacementHelper get(int id) {
		return registry.get(id);
	}

	public static List<IPlacementHelper> getAll() {
		return registryView;
	}

	/** @deprecated use {@link #getAll()} */
	@Deprecated
	public static List<IPlacementHelper> get() {
		return registryView;
	}
}
