package net.sourceforge.fenixedu.persistenceTier.OJB;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fenixedu.domain.Coordinator;
import net.sourceforge.fenixedu.domain.ICoordinator;
import net.sourceforge.fenixedu.domain.IDegreeCurricularPlan;
import net.sourceforge.fenixedu.domain.IExecutionDegree;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.IPersistentCoordinator;

import org.apache.ojb.broker.query.Criteria;

/**
 * fenix-head ServidorPersistente.OJB
 * 
 * @author Jo�o Mota 28/Out/2003
 * @author Francisco Paulo 27/Out/2004 frnp@mega.ist.utl.pt(edit) CoordinatorOJB
 *         class, implements the read methods available for coordinators
 */

public class CoordinatorOJB extends PersistentObjectOJB implements IPersistentCoordinator {

    public List readExecutionDegreesByTeacher(final Integer teacherID) throws ExcepcaoPersistencia {
        final Criteria criteria = new Criteria();
        criteria.addEqualTo("teacher.idInternal", teacherID);

        final List<ICoordinator> coordinators = queryList(Coordinator.class, criteria);
        final List<IExecutionDegree> executionDegrees = new ArrayList(coordinators.size());

        for (final ICoordinator coordinator : coordinators) {
            executionDegrees.add(coordinator.getExecutionDegree());
        }
        return executionDegrees;
    }

    public List readCurricularPlansByTeacher(final Integer teacherID) throws ExcepcaoPersistencia {

        final Criteria criteria = new Criteria();
        criteria.addEqualTo("teacher.idInternal", teacherID);

        final List<ICoordinator> coordinators = queryList(Coordinator.class, criteria);
        final List<IDegreeCurricularPlan> degreeCurricularPlans = new ArrayList();

        for (final ICoordinator coordinator : coordinators) {
            if (!degreeCurricularPlans.contains(coordinator.getExecutionDegree()
                    .getDegreeCurricularPlan())) {
                degreeCurricularPlans.add(coordinator.getExecutionDegree().getDegreeCurricularPlan());
            }
        }
        return degreeCurricularPlans;
    }

    public List readCoordinatorsByExecutionDegree(final Integer executionDegreeID) throws ExcepcaoPersistencia {
        final Criteria criteria = new Criteria();
        criteria.addEqualTo("executionDegree.idInternal", executionDegreeID);
        return queryList(Coordinator.class, criteria);

    }

    public ICoordinator readCoordinatorByTeacherIdAndExecutionDegreeId(final Integer teacherID,
            final Integer executionDegreeId) throws ExcepcaoPersistencia {
        final Criteria criteria = new Criteria();
        criteria.addEqualTo("executionDegree.idInternal", executionDegreeId);
        criteria.addEqualTo("teacher.idInternal", teacherID);
        return (ICoordinator) queryObject(Coordinator.class, criteria);
    }

}