package forestry.core.gui.elements;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IValueElement<V> {

	V getValue();

	void setValue(V value);
}
