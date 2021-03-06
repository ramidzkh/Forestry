package forestry.book.data;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextData {
	public String text = "";

	public String color = "black";
	public boolean bold = false;
	public boolean italic = false;
	public boolean underlined = false;
	public boolean strikethrough = false;
	public boolean obfuscated = false;
	public boolean paragraph = false;
	public boolean dropShadow = false;

	public TextData() {
	}

	public TextData(String text) {
		this.text = text;
	}
}
