/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.RandomFeeder;
import net.azib.ipscan.gui.actions.FeederActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

import static net.azib.ipscan.config.Labels.getLabel;
import static net.azib.ipscan.gui.util.LayoutHelper.formData;

/**
 * GUI for initialization of RandomFeeder
 *
 * @author Anton Keks
 */
public class RandomFeederGUI extends AbstractFeederGUI {

	private Label ipPrototypeLabel;
	private Text ipPrototypeText;
	
	private Label ipMaskLabel;
	private Combo ipMaskCombo;
	
	private Label hostnameLabel;
	private Text hostnameText;
	
	private Button ipUpButton;
	
	private Label countLabel;
	private Spinner countSpinner;

	
	public RandomFeederGUI(Composite parent) {
		super(parent);
		feeder = new RandomFeeder();
	}

	public void initialize(int rowHeight) {
        ipPrototypeLabel = new Label(this, SWT.NONE);
        ipPrototypeText = new Text(this, SWT.BORDER);
        ipMaskLabel = new Label(this, SWT.NONE);
        ipMaskCombo = new Combo(this, SWT.NONE);
        hostnameText = new Text(this, SWT.BORDER);
        hostnameLabel = new Label(this, SWT.NONE);
		ipUpButton = new Button(this, SWT.NONE);
        countLabel = new Label(this, SWT.NONE);
        countSpinner = new Spinner(this, SWT.BORDER);
        
        // the longest possible IP
        ipPrototypeText.setText("255.255.255.255xx");
        int textWidth = ipPrototypeText.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
        ipPrototypeText.setText("");

        ipPrototypeLabel.setText(getLabel("feeder.random.prototype")+":");
        ipPrototypeLabel.setLayoutData(formData(null, new FormAttachment(hostnameLabel, 0, SWT.RIGHT), new FormAttachment(ipPrototypeText, 0, SWT.CENTER), null));
        
        ipPrototypeText.setLayoutData(formData(textWidth, SWT.DEFAULT, new FormAttachment(ipPrototypeLabel), null, new FormAttachment(0), new FormAttachment(0, rowHeight)));
        
        ipMaskLabel.setText(getLabel("feeder.random.mask")+":");
        ipMaskLabel.setLayoutData(formData(new FormAttachment(ipPrototypeText, 3), null, new FormAttachment(ipPrototypeText, 0, SWT.CENTER), null));
        
		ipMaskCombo.setVisibleItemCount(10);
		// Warning: IPv4 specific netmasks
		ipMaskCombo.add("255...128");
		ipMaskCombo.add("255...0");
		ipMaskCombo.add("255..0.0");
		ipMaskCombo.add("255.0.0.0");
		ipMaskCombo.add("0.0.0.0");
		ipMaskCombo.add("255..0.255");
		ipMaskCombo.add("255.0.0.255");
		ipMaskCombo.select(3);
		ipMaskCombo.setLayoutData(formData(textWidth - 15, rowHeight, new FormAttachment(ipMaskLabel), null, new FormAttachment(0), new FormAttachment(ipPrototypeText, 0, SWT.BOTTOM)));
        
		FeederActions.HostnameButton hostnameSelectionListener = new FeederActions.HostnameButton(hostnameText, ipPrototypeText, ipMaskCombo);
        hostnameText.addTraverseListener(hostnameSelectionListener);
		hostnameText.setLayoutData(formData(textWidth, SWT.DEFAULT, new FormAttachment(ipPrototypeText, 0, SWT.LEFT), null, new FormAttachment(ipPrototypeText), new FormAttachment(ipUpButton, 0, SWT.BOTTOM)));
        
        hostnameLabel.setText(getLabel("feeder.random.hostname")+":");
		hostnameLabel.setLayoutData(formData(new FormAttachment(0), null, new FormAttachment(hostnameText, 0, SWT.CENTER), null));

		ipUpButton.setImage(new Image(getDisplay(), Labels.getInstance().getImageAsStream("button.ipUp.img")));
		ipUpButton.setText(getLabel("button.ipUp"));
		ipUpButton.addSelectionListener(hostnameSelectionListener);
		ipUpButton.setLayoutData(formData(SWT.DEFAULT, rowHeight, new FormAttachment(hostnameText), null, new FormAttachment(ipPrototypeText), null));
		
		countLabel.setText(getLabel("feeder.random.count"));
		countLabel.setLayoutData(formData(new FormAttachment(ipUpButton, 3), null, new FormAttachment(ipUpButton, 0, SWT.CENTER), null));
		
		countSpinner.setSelection(100);
		countSpinner.setMaximum(100000);
		countSpinner.setMinimum(1);
		countSpinner.setLayoutData(formData(new FormAttachment(countLabel), new FormAttachment(ipMaskCombo, 0, SWT.RIGHT), new FormAttachment(ipUpButton, 0, SWT.CENTER), null));
		countSpinner.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				// this due to a bug either in SWT or GTK:
				// spinner getText() returns the new value only if
				// it has lost the focus first
				ipPrototypeText.forceFocus();
				countSpinner.forceFocus();
			}
		});
		
		pack();

		// do this stuff asynchronously (to show GUI faster)
		asyncFillLocalHostInfo(hostnameText, ipPrototypeText);
	}

	public Feeder createFeeder() {
		feeder = new RandomFeeder(ipPrototypeText.getText(), ipMaskCombo.getText(), countSpinner.getSelection());
		return feeder;
	}
	
	public String[] serialize() {
		return new String[] {ipPrototypeText.getText(), ipMaskCombo.getText(), String.valueOf(countSpinner.getSelection())};
	}

	public void unserialize(String[] parts) {
		ipPrototypeText.setText(parts[0]);
		ipMaskCombo.setText(parts[1]);
		countSpinner.setSelection(Integer.parseInt(parts[2]));
	}
	
	public String[] serializePartsLabels() {
		return new String[] {"feeder.random.prototype", "feeder.random.mask", "feeder.random.count"};
	}
	
}
