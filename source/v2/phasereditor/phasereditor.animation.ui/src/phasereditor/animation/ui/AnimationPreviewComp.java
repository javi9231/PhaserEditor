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
package phasereditor.animation.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;

import phasereditor.assetpack.core.animations.AnimationModel;
import phasereditor.ui.EditorSharedImages;
import phasereditor.ui.IEditorSharedImages;
import phasereditor.ui.ImageCanvas_Zoom_1_1_Action;
import phasereditor.ui.ImageCanvas_Zoom_FitWindow_Action;

/**
 * @author arian
 *
 */
public class AnimationPreviewComp extends SashForm {

	private AnimationCanvas _animationCanvas;
	private AnimationTimelineCanvas<AnimationModel> _timelineCanvas;
	private AnimationModel _model;
	private ImageCanvas_Zoom_1_1_Action _zoom_1_1_action;
	private ImageCanvas_Zoom_FitWindow_Action _zoom_fitWindow_action;
	private Action _showTimeline;
	private AnimationActions _animationActions;

	public AnimationPreviewComp(Composite parent, int style) {
		super(parent, SWT.VERTICAL | style);

		_animationCanvas = new AnimationCanvas(this, SWT.BORDER);
		_timelineCanvas = new AnimationTimelineCanvas<>(this, SWT.BORDER);
		_timelineCanvas.setAnimationCanvas(_animationCanvas);

		addControlListener(ControlListener.controlResizedAdapter(e -> _animationCanvas.resetZoom()));

		setWeights(new int[] { 2, 1 });

		afterCreateWidgets();

	}

	private void afterCreateWidgets() {

		_animationCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				var anim = getTimelineCanvas().getModel();

				if (anim != null) {
					getTimelineCanvas().clearSelection();
				}
			}
		});

		_animationCanvas.setStepCallback(_timelineCanvas::redraw);

		_animationActions = new AnimationActions(_animationCanvas, _timelineCanvas);

		_animationCanvas.setPlaybackCallback(_animationActions::animationStatusChanged);

		_animationCanvas.addPaintListener(e -> {
			if (_animationCanvas.getModel() != null) {
				e.gc.setAlpha(40);
				e.gc.setForeground(_animationCanvas.getForeground());
				e.gc.drawText(_animationCanvas.getModel().getKey(), 0, 0, true);
			}
		});
	}

	public void setModel(AnimationModel model) {
		_model = model;

		_animationActions.setChecked(false);
		_animationActions.setEnabled(false);
		
		if (_model == null) {

			if (_zoom_1_1_action != null) {
				_zoom_1_1_action.setEnabled(false);
				_zoom_fitWindow_action.setEnabled(false);
			}

			_animationCanvas.setModel(null);
			_timelineCanvas.setModel(null);

			return;
		}

		_animationCanvas.setModel(_model, false);

		_animationActions.getPlayAction().setEnabled(true);

		if (_timelineCanvas.getModel() == _model) {
			_timelineCanvas.redraw();
		} else {
			_timelineCanvas.setModel(_model);
		}

		if (_zoom_1_1_action != null) {
			_zoom_1_1_action.setEnabled(true);
			_zoom_fitWindow_action.setEnabled(true);
		}
	}

	public AnimationModel getModel() {
		return _model;
	}

	public AnimationCanvas getAnimationCanvas() {
		return _animationCanvas;
	}

	public AnimationTimelineCanvas<AnimationModel> getTimelineCanvas() {
		return _timelineCanvas;
	}

	private void disableToolbar() {
		_animationActions.setEnabled(false);

		if (_zoom_1_1_action != null) {
			_zoom_1_1_action.setEnabled(false);
			_zoom_fitWindow_action.setEnabled(false);
		}
	}

	public void createToolBar(IToolBarManager manager) {

		_showTimeline = new Action("Timeline",
				EditorSharedImages.getImageDescriptor(IEditorSharedImages.IMG_APPLICATION_SPLIT)) {
			@Override
			public void run() {
				if (getMaximizedControl() == null) {
					setMaximizedControl(getAnimationCanvas());
				} else {
					setMaximizedControl(null);
				}
			}
		};

		_showTimeline.setChecked(true);

		_zoom_1_1_action = new ImageCanvas_Zoom_1_1_Action(_animationCanvas);
		_zoom_fitWindow_action = new ImageCanvas_Zoom_FitWindow_Action(_animationCanvas);

		createPlaybackToolbar(manager);

		manager.add(new Separator());
		manager.add(_showTimeline);
		manager.add(new Separator());
		manager.add(_zoom_1_1_action);
		manager.add(_zoom_fitWindow_action);

		disableToolbar();
	}

	public void createPlaybackToolbar(IToolBarManager manager) {
		_animationActions.fillToolbar(manager);
	}

}
