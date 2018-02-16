// The MIT License (MIT)
//
// Copyright (c) 2015, 2018 Arian Fornaris
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions: The above copyright notice and this permission
// notice shall be included in all copies or substantial portions of the
// Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.
package phasereditor.canvas.ui.editors.edithandlers;

import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.paint.Color;
import phasereditor.canvas.ui.editors.operations.ChangePropertyOperation;
import phasereditor.canvas.ui.editors.operations.CompositeOperation;
import phasereditor.canvas.ui.shapes.IObjectNode;

/**
 * @author arian
 *
 */
public class AngleShortucsPane extends ShortcutPane {

	private Label _angleLabel;

	@SuppressWarnings("boxing")
	public AngleShortucsPane(IObjectNode object) {
		super(object);

		String[] values = { "-45", "+45", "0" };

		_angleLabel = new Label();
		_angleLabel.setMinWidth(150);
		_angleLabel.setMaxWidth(150);
		_angleLabel.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
		_angleLabel.setTextFill(Color.WHITE);
		
		add(_angleLabel, 0, 0);
		setColumnSpan(_angleLabel, 3);

		int i = 0;

		for (String value : values) {
			Btn btn = new Btn(value);
			add(btn, i, 1);
			i++;
		}
	}

	@Override
	public void updateHandler() {
		
		_angleLabel.setText(Double.toString(_model.getAngle()));
		
		super.updateHandler();
	}

	class Btn extends ShortcutButton {
		private double _value;

		public Btn(String label) {
			_value = Double.parseDouble(label);

			Label graphic = new Label(label);
			graphic.setTextFill(Color.WHITE);
			//graphic.setEffect(new DropShadow(1, Color.WHITE));
			setGraphic(graphic);

			setSize(50, -1);
		}

		@Override
		protected void doAction() {
			CompositeOperation operations = new CompositeOperation();
			double angle = _model.getAngle() + _value;

			if (_value == 0) {
				angle = 0;
			}

			String id = _model.getId();

			operations.add(new ChangePropertyOperation<Number>(id, "angle", Double.valueOf(angle)));

			_canvas.getUpdateBehavior().executeOperations(operations);

		}

	}

	@Override
	public boolean isValid() {
		return true;
	}

}