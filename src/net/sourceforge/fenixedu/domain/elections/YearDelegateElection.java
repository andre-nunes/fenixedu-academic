package net.sourceforge.fenixedu.domain.elections;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.fenixedu.domain.CurricularYear;
import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.DegreeCurricularPlan;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.student.Student;

import org.joda.time.YearMonthDay;

public class YearDelegateElection extends YearDelegateElection_Base {

    /*
     * When created, must have the following attributes specified
     */
    public YearDelegateElection(ExecutionYear executionYear, Degree degree, CurricularYear curricularYear,
	    DelegateElectionCandidacyPeriod candidacyPeriod) {
	super();

	setExecutionYear(executionYear);
	setDegree(degree);
	setCurricularYear(curricularYear);

	/*
	 * Must be invoked after setExecutionYear
	 */
	setCandidacyPeriod(candidacyPeriod);
    }

    @Override
    public void delete() {
	removeCurricularYear();
	super.delete();
    }

    @Override
    public void setCandidacyPeriod(DelegateElectionCandidacyPeriod candidacyPeriod) {
	if (candidacyPeriod != null) {
	    validatePeriodGivenExecutionYear(getExecutionYear(), candidacyPeriod);
	    if (hasLastVotingPeriod())
		if (candidacyPeriod.endsBefore(getLastVotingPeriod())) {
		    throw new DomainException("error.elections.edit.colidesWithVotingPeriod", new String[] {
			    getDegree().getSigla(), getCurricularYear().getYear().toString(), candidacyPeriod.getPeriod(),
			    getLastVotingPeriod().getPeriod() });
		}
	}

	super.setCandidacyPeriod(candidacyPeriod);

    }

    @Override
    public void addVotingPeriod(DelegateElectionVotingPeriod votingPeriod) {
	if (votingPeriod != null) {
	    validatePeriodGivenExecutionYear(getExecutionYear(), votingPeriod);

	    if (!hasCandidacyPeriod()) {
		throw new DomainException("error.elections.createVotingPeriod.mustCreateCandidacyPeriod", new String[] {
			getDegree().getSigla(), getCurricularYear().getYear().toString() });
	    }

	    if (!getCandidacyPeriod().endsBefore(votingPeriod)) {
		throw new DomainException("error.elections.edit.colidesWithCandidacyPeriod", new String[] {
			getDegree().getSigla(), getCurricularYear().getYear().toString(), getCandidacyPeriod().getPeriod(),
			votingPeriod.getPeriod() });
	    }
	    if (!getLastElectionPeriod().endsBefore(votingPeriod)) {
		throw new DomainException("error.elections.edit.colidesWithPreviousVotingPeriod", new String[] {
			getDegree().getSigla(), getCurricularYear().getYear().toString(), getCandidacyPeriod().getPeriod(),
			votingPeriod.getPeriod() });

	    }
	}
	super.addVotingPeriod(votingPeriod);
    }

    /*
     * Checks if given period belongs to given execution year
     */
    private void validatePeriodGivenExecutionYear(ExecutionYear executionYear, DelegateElectionPeriod period) {
	if (period.getStartDate().isBefore(executionYear.getBeginDateYearMonthDay())
		|| period.getEndDate().isAfter(executionYear.getEndDateYearMonthDay())) {
	    throw new DomainException("error.elections.setPeriod.invalidPeriod", new String[] { getDegree().getSigla(),
		    getCurricularYear().getYear().toString(), period.getPeriod(), executionYear.getYear() });
	}
    }

    public boolean getCanYearDelegateBeElected() {
	return (getLastVotingPeriod().isCurrentPeriod() || hasElectedStudent() ? false : true);
    }

    /*
     * Checks if the new election candidacy period colides with another election
     * candidacy period, previously added to this degree and curricular year
     */
    private static void checkNewElectionCandidacyPeriod(Degree degree, ExecutionYear executionYear,
	    CurricularYear curricularYear, DelegateElectionCandidacyPeriod candidacyPeriod) throws DomainException {

	for (DelegateElection election : degree.getYearDelegateElectionsGivenExecutionYearAndCurricularYear(executionYear,
		curricularYear)) {
	    if (!election.getCandidacyPeriod().endsBefore(candidacyPeriod)) {
		throw new DomainException("error.elections.newElection.invalidPeriod", new String[] { degree.getSigla(),
			curricularYear.getYear().toString(), candidacyPeriod.getPeriod(),
			election.getCandidacyPeriod().getPeriod() });
	    }
	}
    }

    /*
     * If there is a voting period ocurring, it's not possible to add a new
     * election
     */
    private static void checkPreviousDelegateElectionExistence(Degree degree, CurricularYear curricularYear,
	    ExecutionYear executionYear) throws DomainException {

	final DelegateElection previousElection = degree.getYearDelegateElectionWithLastCandidacyPeriod(executionYear,
		curricularYear);
	if (previousElection != null && previousElection.hasLastVotingPeriod()
		&& previousElection.getLastVotingPeriod().isCurrentPeriod()) {
	    throw new DomainException("error.elections.newElection.currentVotingPeriodExists", new String[] { degree.getSigla(),
		    curricularYear.getYear().toString(), previousElection.getLastVotingPeriod().getPeriod() });
	}

	if (previousElection != null && previousElection.getVotingPeriod() != null
		&& !previousElection.getLastVotingPeriod().isPastPeriod()) {
	    // future voting period (must be deleted)
	    previousElection.getLastVotingPeriod().delete();
	}
    }

    /*
     * DOMAIN SERVICES
     */
    public static YearDelegateElection createDelegateElectionWithCandidacyPeriod(Degree degree, ExecutionYear executionYear,
	    YearMonthDay candidacyPeriodStartDate, YearMonthDay candidacyPeriodEndDate, CurricularYear curricularYear) {

	DelegateElectionCandidacyPeriod period = new DelegateElectionCandidacyPeriod(candidacyPeriodStartDate,
		candidacyPeriodEndDate);
	checkNewElectionCandidacyPeriod(degree, executionYear, curricularYear, period);

	/* Checks if there is a previous delegate election and its state */
	checkPreviousDelegateElectionExistence(degree, curricularYear, executionYear);

	YearDelegateElection election = new YearDelegateElection(executionYear, degree, curricularYear, period);

	/* Add degree students to election students list */
	for (DegreeCurricularPlan dcp : degree.getActiveDegreeCurricularPlans()) {
	    for (StudentCurricularPlan scp : dcp.getActiveStudentCurricularPlans()) {
		if (scp.getRegistration().getCurricularYear(executionYear) == curricularYear.getYear()) {
		    if (!hasDelegateElection(election, scp)) {
			election.addStudents(scp.getRegistration().getStudent());
		    }
		}
	    }
	}

	return election;
    }

    private static boolean hasDelegateElection(YearDelegateElection election, StudentCurricularPlan scp) {
	for (DelegateElection delegateElection : scp.getRegistration().getStudent().getDelegateElections()) {
	    if (delegateElection instanceof YearDelegateElection) {
		if (delegateElection.getDegree().equals(election.getDegree())
			&& delegateElection.getExecutionYear().equals(election.getExecutionYear())
			&& !delegateElection.getLastVotingPeriod().isPastPeriod()) {
		    return true;
		}
	    }
	}
	return false;
    }

    @Override
    public void createVotingPeriod(YearMonthDay startDate, YearMonthDay endDate) {
	if (hasLastVotingPeriod()) {
	    if (getLastVotingPeriod().isPastPeriod() && !getLastVotingPeriod().isFirstRoundElections()) {
		throw new DomainException("error.elections.createVotingPeriod.mustCreateNewCandidacyPeriod", new String[] {
			getDegree().getSigla(), getCurricularYear().getYear().toString() });
	    }
	    if (getLastVotingPeriod().isCurrentPeriod()) {
		throw new DomainException("error.elections.createVotingPeriod.onlyCanExtendPeriod", new String[] {
			getDegree().getSigla(), getCurricularYear().getYear().toString() });
	    }
	    if (hasVotingPeriodIntersecting(startDate, endDate)) {
		throw new DomainException("error.elections.createVotingPeriod.votingPeriodIntersecting", new String[] {
			getDegree().getSigla(), getCurricularYear().getYear().toString() });
	    }
	}

	DelegateElectionVotingPeriod period = new DelegateElectionVotingPeriod(startDate, endDate);
	addVotingPeriod(period);
    }

    @Override
    public void editCandidacyPeriod(final YearMonthDay startDate, final YearMonthDay endDate) {
	final DelegateElectionCandidacyPeriod candidacyPeriod = getCandidacyPeriod();
	final DelegateElectionVotingPeriod votingPeriod = getVotingPeriod(startDate, endDate);

	if (candidacyPeriod.isPastPeriod() && votingPeriod != null && votingPeriod.getStartDate().isBefore(new YearMonthDay())) {
	    throw new DomainException("error.yearDelegateElections.edit.pastPeriod", new String[] { getDegree().getSigla(),
		    getCurricularYear().getYear().toString(), getCandidacyPeriod().getPeriod() });
	} else {
	    try {
		candidacyPeriod.delete();
		setCandidacyPeriod(new DelegateElectionCandidacyPeriod(startDate, endDate));
	    } catch (DomainException ex) {
		throw new DomainException(ex.getMessage(), ex.getArgs());
	    }
	}
    }

    @Override
    public void editVotingPeriod(YearMonthDay startDate, YearMonthDay endDate, DelegateElectionVotingPeriod votingPeriod) {
	if (!endDate.isAfter(getLastVotingEndDate()))
	    throw new DomainException("error.elections.edit.newEndDateMustBeGreater", getDegree().getSigla(), getCurricularYear()
		    .getYear().toString());

	if (!votingPeriod.isPastPeriod()) {
	    votingPeriod.setEndDate(endDate);
	} else {
	    throw new DomainException("error.yearDelegateElections.edit.pastPeriod", new String[] { getDegree().getSigla(),
		    getCurricularYear().getYear().toString(), votingPeriod.getPeriod() });
	}
    }

    @Override
    public void deleteCandidacyPeriod() {
	if (!getCandidacyPeriod().isPastPeriod()) {
	    this.delete();
	} else {
	    throw new DomainException("error.yearDelegateElections.delete.pastPeriod", new String[] { getDegree().getSigla(),
		    getCurricularYear().getYear().toString(), getCandidacyPeriod().getPeriod() });
	}
    }

    @Override
    public void deleteVotingPeriod(DelegateElectionVotingPeriod votingPeriod, boolean removeElection) {

	if (!votingPeriod.isPastPeriod() && !votingPeriod.isCurrentPeriod()) {
	    super.deleteVotingPeriod(votingPeriod);
	    if (removeElection) {
		this.deleteCandidacyPeriod();
	    }
	} else {
	    throw new DomainException("error.yearDelegateElections.delete.pastPeriod", new String[] { getDegree().getSigla(),
		    getCurricularYear().getYear().toString(),
		    getVotingPeriod(votingPeriod.getStartDate(), votingPeriod.getEndDate()).getPeriod() });
	}

    }

    public DelegateElectionVotingPeriod getCurrentVotingPeriod() {
	for (DelegateElectionVotingPeriod votingPeriod : getVotingPeriod()) {
	    if (votingPeriod.isCurrentPeriod()) {
		return votingPeriod;
	    }
	}
	return null;
    }

    @Override
    public List<Student> getCandidates() {
	if (!hasLastVotingPeriod() || getLastVotingPeriod().isFirstRoundElections()) {
	    return super.getCandidates();
	}
	return getLastVotingPeriod().getCandidatesForNewRoundElections();

    }

    @Override
    public List<Student> getNotCandidatedStudents() {
	if (!hasLastVotingPeriod() || getLastVotingPeriod().isFirstRoundElections()) {
	    return super.getNotCandidatedStudents();
	}
	// Don't have candidates
	return new LinkedList<Student>();
    }

}
