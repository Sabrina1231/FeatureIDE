/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2011  FeatureIDE Team, University of Magdeburg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.fm.ui.editors.featuremodel.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ovgu.featureide.fm.core.FeatureModel;

/**
 * Creates a new propositional constraint below the feature diagram.
 * 
 * @author Christian Becker
 * @author Thomas Thuem
 */
public class CreateConstraintAction extends AbstractConstraintEditorAction {

	private static ImageDescriptor createImage = PlatformUI.getWorkbench()
			.getSharedImages()
			.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD);

	public CreateConstraintAction(Object viewer,
			FeatureModel featuremodel) {
		super(viewer, featuremodel, "Create Constraint");
		setImageDescriptor(createImage);
	}

	@Override
	protected boolean isValidSelection(IStructuredSelection selection) {
		return true;
	}

	@Override
	public void run() {
		super.run();
		openEditor(null);
	}

}
