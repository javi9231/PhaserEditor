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
package phasereditor.assetpack.ui.preview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.Rectangle;

import phasereditor.assetpack.core.AtlasAssetModel;
import phasereditor.assetpack.core.AtlasAssetModel.Frame;
import phasereditor.ui.IFrameProvider;

/**
 * @author arian
 *
 */
public class AtlasAssetFramesProvider implements IFrameProvider {

	private AtlasAssetModel _asset;
	private List<Frame> _frames;
	private IFile _file;

	public AtlasAssetFramesProvider(AtlasAssetModel asset) {
		super();
		_asset = asset;
		_frames = new ArrayList<>(asset.getAtlasFrames());
		_file = _asset.getTextureFile();
	}

	@Override
	public int getFrameCount() {
		return _frames.size();
	}

	@Override
	public Rectangle getFrameRectangle(int index) {
		return getFrameObject(index).getFrameData().src;
	}

	@Override
	public IFile getFrameImageFile(int index) {
		return _file;
	}

	@Override
	public String getFrameTooltip(int index) {
		var rect = getFrameRectangle(index);
		return rect.width + "x" + rect.height;
	}

	@Override
	public Frame getFrameObject(int index) {
		return _frames.get(index);
	}

	@Override
	public String getFrameLabel(int index) {
		var frame = _frames.get(index);
		if (frame == null) {
			return null;
		}
		return frame.getKey();
	}
}
