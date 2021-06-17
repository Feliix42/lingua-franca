package org.lflang.diagram.synthesis.action

import de.cau.cs.kieler.klighd.IAction
import de.cau.cs.kieler.klighd.kgraph.KNode
import org.lflang.diagram.synthesis.LinguaFrancaSynthesis

import static extension de.cau.cs.kieler.klighd.util.ModelingUtil.*
import static extension org.lflang.diagram.synthesis.action.MemorizingExpandCollapseAction.*

/**
 * Action that expands (shows details) of all reactor nodes.
 * 
 * @author{Alexander Schulz-Rosengarten <als@informatik.uni-kiel.de>}
 */
class CollapseAllReactorsAction extends AbstractAction {
    
    public static val ID = "org.lflang.diagram.synthesis.action.CollapseAllReactorsAction"
    
    override execute(ActionContext context) {
        val vc = context.viewContext
        for (node : vc.viewModel.eAllContentsOfType(KNode).filter[sourceIsReactor].toIterable) {
        	if (!(node.sourceAsReactor().main || node.sourceAsReactor().federated)) { // Do not collapse main reactor
            	node.setExpansionState(node.getProperty(LinguaFrancaSynthesis.REACTOR_INSTANCE), vc.viewer, false)
            }
        }
        return IAction.ActionResult.createResult(true);
    }
    
}
