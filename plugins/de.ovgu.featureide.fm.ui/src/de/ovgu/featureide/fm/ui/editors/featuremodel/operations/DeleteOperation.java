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
package de.ovgu.featureide.fm.ui.editors.featuremodel.operations;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

import de.ovgu.featureide.fm.core.Constraint;
import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.fm.ui.FMUIPlugin;
import de.ovgu.featureide.fm.ui.editors.featuremodel.editparts.ConstraintEditPart;
import de.ovgu.featureide.fm.ui.editors.featuremodel.editparts.FeatureEditPart;
import de.ovgu.featureide.fm.ui.editors.featuremodel.editparts.LegendEditPart;

/**
 * Operation with functionality to delete multiple elements from the feature
 * model editor. Enables Undo/Redo.
 * 
 * @author Fabian Benduhn
 */
public class DeleteOperation extends AbstractOperation {

	private static final String LABEL = "Delete";
	private GraphicalViewerImpl viewer;
	private FeatureModel featureModel;
	private List<AbstractOperation> operations;

	/**
	 * 
	 */
	public DeleteOperation(GraphicalViewerImpl viewer, FeatureModel featureModel) {
		super(LABEL);
		this.viewer = viewer;
		this.featureModel = featureModel;
		this.operations = new LinkedList<AbstractOperation>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse
	 * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		AbstractOperation op = null;
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();

		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			Object editPart = iter.next();

			if (editPart instanceof ConstraintEditPart) {

				Constraint constraint = ((ConstraintEditPart) editPart)
						.getConstraintModel();
				op = new ConstraintDeleteOperation(constraint, featureModel);

				executeOperation(op);
			}
			if (editPart instanceof LegendEditPart) {
				op = new LegendHideOperation(featureModel);

				executeOperation(op);

			}
			if (editPart instanceof FeatureEditPart) {
				Feature feature = ((FeatureEditPart) editPart)
						.getFeatureModel();
				op = new FeatureDeleteOperation(featureModel, feature);

				executeOperation(op);

			}
			operations.add(op);
		}
		featureModel.handleModelDataChanged();
		return Status.OK_STATUS;
	}

	/**
	 * @param op
	 *            operation to be executed
	 */
	private void executeOperation(AbstractOperation op) {
		try {
			PlatformUI.getWorkbench().getOperationSupport()
					.getOperationHistory().execute(op, null, null);

		} catch (ExecutionException e) {
			FMUIPlugin.getDefault().logError(e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse
	 * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		List<AbstractOperation> ops = new LinkedList<AbstractOperation>();
		ops.addAll(operations);
		Collections.reverse(operations);
		while (!ops.isEmpty()) {
			for (AbstractOperation op : operations) {
				try {

					op.redo(monitor, info);
					ops.remove(op);

				} catch (Exception E) {

				}

			}
		}
		featureModel.handleModelDataChanged();
		featureModel.redrawDiagram();

		return Status.OK_STATUS;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse
	 * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus undo(IProgressMonitor arg0, IAdaptable arg1)
			throws ExecutionException {
		List<AbstractOperation> ops = new LinkedList<AbstractOperation>();
		ops.addAll(operations);
		Collections.reverse(operations);
		while (!ops.isEmpty()) {
			for (AbstractOperation op : operations) {

				if (op.canUndo()) {
					op.undo(arg0, arg1);
					ops.remove(op);
				}
			}
		}
		featureModel.handleModelDataLoaded();
		return Status.OK_STATUS;
	}

}