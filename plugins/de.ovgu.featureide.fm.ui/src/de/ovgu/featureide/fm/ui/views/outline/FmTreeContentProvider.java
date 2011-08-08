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
package de.ovgu.featureide.fm.ui.views.outline;


import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ovgu.featureide.fm.core.Constraint;
import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;

/**
 * This class is part of the outline. It provides the content that
 * should be displayed. Therefore it maps the information provided
 * by the fmProject to the methods of the ITreeContentProvider
 * interface.
 * 
 * @author Jan Wedding
 * @author Melanie Pflaume
 */
public class FmTreeContentProvider implements ITreeContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	private FeatureModel fModel;
	
	@Override
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		fModel = ((FeatureModel)newInput);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		Object[] elements;
		if (fModel instanceof FeatureModel && fModel != null) {
			elements = new Object[2];
//			if (fModel.getRoot().isOr())
//				elements[0] = new Object[] {new FmOutlineGroupStateStorage(fModel.getRoot(), true) };
//			else if (fModel.getRoot().isAlternative())
//				elements[0] = new Object[] {new FmOutlineGroupStateStorage(fModel.getRoot(), false) };
//			else 
				elements[0] = fModel.getRoot();
			
			elements[1] = "Constraints";
			return elements;
		}
		
		return new String[] {
				"No data to display available." };
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement == null) return null;
		
		if (parentElement instanceof String) {
			Object[] elements = new Object[fModel.getConstraintCount()];
			List<Constraint> cList = fModel.getConstraints();
			for (int i = 0; i < fModel.getConstraintCount(); i++) {
				elements[i] = cList.get(i);
			}
			return elements;
		}
		
		if (parentElement instanceof FmOutlineGroupStateStorage) {
			return featureListToArray(((FmOutlineGroupStateStorage)parentElement).getFeature().getChildren());
		}

		if (!(parentElement instanceof Feature)) return null;
		if (!((Feature)parentElement).hasChildren()) return null;
		
		if (((Feature)parentElement).isOr())
			return new Object[] {new FmOutlineGroupStateStorage((Feature) parentElement, true) };
		if (((Feature)parentElement).isAlternative())
			return new Object[] {new FmOutlineGroupStateStorage((Feature) parentElement, false) };
		
		return featureListToArray(((Feature)parentElement).getChildren());
	}
	
	private Feature[] featureListToArray(LinkedList<Feature> f) {
		Feature[] fArray = new Feature[f.size()];
		for (int i = 0; i < f.size(); i++) {
			fArray[i] = f.get(i);
		}
		return fArray;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof Feature)
			return ((Feature)element).getParent();
		else if (element instanceof Constraint) 
			return "Constraints";
		else if (element instanceof FmOutlineGroupStateStorage) 
			return ((FmOutlineGroupStateStorage)element).getFeature();
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Feature) 
			return ((Feature)element).hasChildren();
		else if (element instanceof String) 
			return true;
		else if (element instanceof FmOutlineGroupStateStorage)
			return true;
		return false;
	}
	
}