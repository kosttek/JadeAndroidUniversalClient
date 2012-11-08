/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package examples.pl.edu.kosttek.jadetutorial;

import jade.core.AID;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;

import examples.pl.edu.kosttek.jadetutorial.QueueAgent.OnDataChange;

/**
  @author Giovanni Caire - TILAB
 */
class QueueGui extends JFrame {	
	private QueueAgent myAgent;
	private String text="witam!"; //TODO to nie musi byc globalne
	private JTextField titleField, priceField;
	private JTextArea textArea2;
	private JLabel current,sizeLabel;
	
	private OnDataChange onDataChange = new OnDataChange() {
		
		@Override
		public void change() {
			setSizeLabel( myAgent.getQueueSize()+"");
			setCurrent(myAgent.getCurrent()+"");
			HashMap<Integer, AID> map = myAgent.getQueueMap();
			text = new String();
			for(Integer  inte : map.keySet() ){
				text+= inte+". "+map.get(inte)+"\n";
			}
			textArea2.setText(text);
			repaint();
		}
	};
	
	QueueGui(QueueAgent a) {
		super(a.getLocalName());
		
		myAgent = a;
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 2));

//		p.add(new JLabel("Book title:"));
//		titleField = new JTextField(15);
//		p.add(titleField);
//		p.add(new JLabel("Price:"));
//		priceField = new JTextField(15);
//		p.add(priceField);
		
		setCurrent(new JLabel("yo"));
//		getCurrent().setPreferredSize(new Dimension(80, 40));
		p.add(getCurrent());
		setSizeLabel(new JLabel("waza"));
//		getSizeLabel().setPreferredSize(new Dimension(80, 40));
		p.add(getSizeLabel());
		getContentPane().add(p, BorderLayout.NORTH);

		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout());
		textArea2 = new JTextArea(text, 15, 30);
	    textArea2.setPreferredSize(new Dimension(300, 100));
	    JScrollPane scrollPane = new JScrollPane(textArea2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    textArea2.setLineWrap(true);
	    p2.add(scrollPane);

		
		getContentPane().add(p2, BorderLayout.CENTER);
		
		JButton addButton = new JButton("Next");
		addButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					myAgent.incrementCurrent();
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(QueueGui.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
				}
			}
		} );
		p = new JPanel();
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);
		
		// Make the agent terminate when the user closes 
		// the GUI using the button on the upper right corner	
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );
		
		setResizable(false);
	}
	
	public void showGui() {
		
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
		
	}

	public JLabel getCurrent() {
		return current;
	}

	public void setCurrent(JLabel text) {
		this.current = text;
	}	
	public void setCurrent(String text) {
		this.current.setText(text);
		System.out.println(text);
//		repaint();
	}

	public JLabel getSizeLabel() {
		return sizeLabel;
	}

	public void setSizeLabel(JLabel size) {
		this.sizeLabel = size;
	}
	public void setSizeLabel(String text) {
		this.sizeLabel.setText(text);
		System.out.println(text);
//		repaint();
	}

	public OnDataChange getOnDataChange() {
		return onDataChange;
	}

	public void setOnDataChange(OnDataChange onDataChange) {
		this.onDataChange = onDataChange;
	}
	
	
}
