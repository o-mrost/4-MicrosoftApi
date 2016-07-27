import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class FocusedTitleListCellRenderer implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		// TODO Auto-generated method stub
		return null;
	}

}


//class FocusedTitleListCellRenderer implements ListCellRenderer {
//	  protected static Border noFocusBorder = new EmptyBorder(15, 1, 1, 1);
//
//	  protected static TitledBorder focusBorder = new TitledBorder(LineBorder.createGrayLineBorder(),
//	      "title");
//
//	  protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
//
//	  public Component getListCellRendererComponent(JList list, Object value, int index,
//	      boolean isSelected, boolean cellHasFocus) {
//	    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
//	        isSelected, cellHasFocus);
//	    renderer.setBorder(cellHasFocus ? focusBorder : noFocusBorder);
//	    return renderer;
//	  }
//	}