package net.sourceforge.fenixedu.domain.reports;

import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.student.Registration;
import net.sourceforge.fenixedu.domain.student.registrationStates.RegistrationState;
import net.sourceforge.fenixedu.domain.student.registrationStates.RegistrationStateType;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet.Row;

public class FlunkedReportFile extends FlunkedReportFile_Base {

    public FlunkedReportFile() {
	super();
    }

    @Override
    public String getJobName() {
	return "Listagem de prescri��es";
    }

    @Override
    protected String getPrefix() {
	return "prescricoes";
    }

    @Override
    public void renderReport(Spreadsheet spreadsheet) {
	spreadsheet.setHeader("n�mero aluno");
	setDegreeHeaders(spreadsheet);

	for (final Degree degree : Degree.readNotEmptyDegrees()) {
	    if (checkDegreeType(getDegreeType(), degree)) {
		for (final Registration registration : degree.getRegistrationsSet()) {
		    for (final RegistrationState registrationState : registration.getRegistrationStates()) {
			final RegistrationStateType registrationStateType = registrationState.getStateType();
			if (registrationStateType == RegistrationStateType.FLUNKED
				&& registrationState.getExecutionYear() == getExecutionYear()) {
			    final Row row = spreadsheet.addRow();
			    row.setCell(registration.getNumber());
			    setDegreeCells(row, degree);
			}
		    }
		}
	    }
	}
    }

}
