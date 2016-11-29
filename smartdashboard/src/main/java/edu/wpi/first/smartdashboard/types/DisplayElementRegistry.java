package edu.wpi.first.smartdashboard.types;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.gui.elements.DefaultDisplayElementRegistrar;
import edu.wpi.first.smartdashboard.livewindow.elements.LiveWindowWidgetRegistrar;
import edu.wpi.first.smartdashboard.types.named.PIDCommandType;
import edu.wpi.first.smartdashboard.types.named.PIDSubsystemType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class contains several {@code static} methods that are used to
 * dynamically link widgets to the types of data that they can display.
 * 
 * @author Joe Grinstead
 */
public class DisplayElementRegistry {

	/** The widgets that we have available */
	private static final Set<Class<? extends StaticWidget>> staticWidgets = new LinkedHashSet<Class<? extends StaticWidget>>();

	/** Maps types to the widgets that explicitly support that type */
	private static final Map<DataType, Set<Class<? extends Widget>>> map = new HashMap<DataType, Set<Class<? extends Widget>>>();

	/** Maps the widgets to the types that they explicitly support */
	private static final Map<Class<? extends Widget>, DataType[]> declaredTypes = new HashMap<Class<? extends Widget>, DataType[]>();

	/**
	 * Forces the DisplayElementRegistry to register all widgets and types
	 * supplied in SmartDashboard. This method does <b>not</b> read through the
	 * file system to find extensions, that is done by {@link FileSniffer}.
	 */
	static{
		DefaultDisplayElementRegistrar.init();
        LiveWindowWidgetRegistrar.init();

		// There is a awkward problem where if there is no widget that
		// explicitly supports a type,
		// the type must be initialized manually
		PIDCommandType.get();
		PIDSubsystemType.get();
	}

	/**
	 * Adds the new {@link StaticWidget} to the registry.
	 * 
	 * @param clazz
	 *            the class of the {@link StaticWidget}. If it is an abstract
	 *            class, then it will be ignored.
	 */
	public static void registerStaticWidget(Class<? extends StaticWidget> clazz) {
		if (!Modifier.isAbstract(clazz.getModifiers())) {
			staticWidgets.add(clazz);
		}
		// TODO made this complain if given a normal widget, or make it call
		// registerWidget
	}

	/**
	 * Adds the new {@link Widget} to the registry. There are plenty of
	 * requirements that a {@link Widget} must satisfy, because the
	 * {@link DisplayElementRegistry} relies heavily on reflection in order to
	 * simplify code for an extension developer. Make sure to look at what will
	 * cause a {@link RuntimeException}
	 * 
	 * @param clazz
	 *            the class of the {@link Widget}. If it is an abstract class,
	 *            then it will be ignored.
	 * @throws RuntimeException
	 *             there are several ways that this will be thrown.
	 * 
	 *             <p>
	 *             <ul>
	 *             <li>If there is no TYPES field
	 *             <li>If the TYPES field is not static
	 *             <li>If the TYPES field is not final
	 *             <li>If the TYPES field is not public
	 *             <li>If the TYPES field is not an array of {@link DataType}
	 *             <li>If the TYPES field is null
	 *             </ul>
	 *             </p>
	 */
	public static void registerWidget(Class<? extends Widget> clazz) {
		if (Modifier.isAbstract(clazz.getModifiers())) {
			return;
		}

		DataType[] types = null;

		try {
			Field field = clazz.getDeclaredField("TYPES");
			int modifiers = field.getModifiers();
			if (!Modifier.isStatic(modifiers)) {
				throw new RuntimeException("TYPES must be static");
			} else if (!Modifier.isFinal(modifiers)) {
				throw new RuntimeException("TYPES must be final");
			}
			types = (DataType[]) field.get(null);
			declaredTypes.put(clazz, types);
		} catch (IllegalArgumentException ex) {
			assert false;
		} catch (IllegalAccessException ex) {
			throw new RuntimeException("TYPES must be public");
		} catch (NoSuchFieldException ex) {
			throw new RuntimeException("Every ValueBasedDisplayElement must have a TYPES static field of type DataType[]");
		} catch (SecurityException ex) {
			ex.printStackTrace();
			return;
		} catch (ClassCastException ex) {
			throw new RuntimeException("TYPES must be of type Type[]");
		}

		if (types == null) {
			throw new RuntimeException("TYPES must not be null");
		}

		for (DataType type : types) {
			Set<Class<? extends Widget>> list = map.get(type);
			if (list == null) {
				map.put(type, list = new LinkedHashSet<Class<? extends Widget>>());
			}
			list.add(clazz);
		}
	}

	/**
	 * Used internally to generate the types that a widget supports. Widgets
	 * support more than what they explicitly support, they also deal with
	 * parent types and what not, so that is what this will figure out.
	 * 
	 * @param set
	 *            the set that we will add the types to
	 * @param types
	 *            the types that a widget supports
	 * @return the set that was given (except it will have types added to it)
	 */
	private static Set<DataType> generateTypes(Set<DataType> set, DataType[] types) {
		for (DataType type : types) {
			if (set.add(type)) {
				generateTypes(set, type.getParents());
			}
		}
		return set;
	}

	/**
	 * Returns all the widgets which support a given type.
	 * 
	 * @param type
	 *            the type to support
	 * @return the widgets which support the given type
	 */
	public static Set<Class<? extends Widget>> getWidgetsForType(DataType type) {
		Set<DataType> types = generateTypes(new LinkedHashSet<DataType>(), type.getParents());
		types.add(type);

		Set<Class<? extends Widget>> elements = new LinkedHashSet<Class<? extends Widget>>();

		for (DataType t : types) {
			Set<Class<? extends Widget>> set = map.get(t);
			if (set != null) {
				Class<? extends Widget> priority = t.getDefault();
				if (priority != null) {
					elements.add(priority);
				}

			}
		}
		for (DataType t : types) {
			Set<Class<? extends Widget>> set = map.get(t);
			if (set != null) {
				elements.addAll(set);
			}
		}

		return elements;
	}
    
	/**
	 * Returns all the {@link StaticWidget StaticWidgets} that are registered.
	 */
	public static Set<Class<? extends StaticWidget>> getStaticWidgets() {
		return staticWidgets;
	}

	/**
	 * Returns whether a {@link Widget} of the given class can handle an element
	 * of the given type.
	 * 
	 * @param clazz
	 *            the class of the {@link Widget}
	 * @param type
	 *            the {@link DataType} that we are seeing if the {@link Widget}
	 *            supports
	 * @return whether or not it is a supported type
	 */
	public static boolean supportsType(Class<? extends Widget> clazz, DataType type) {
		for (DataType declaredType : declaredTypes.get(clazz)) {
			if (type.isChildOf(declaredType)) {
				return true;
			}
		}
		return false;
	}
}
