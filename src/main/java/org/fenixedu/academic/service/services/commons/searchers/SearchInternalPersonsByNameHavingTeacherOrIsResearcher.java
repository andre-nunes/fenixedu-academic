/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.service.services.commons.searchers;

import java.util.Collection;
import java.util.HashSet;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.person.RoleType;

public class SearchInternalPersonsByNameHavingTeacherOrIsResearcher extends SearchParties<Person> {

    @Override
    protected Collection<Person> search(String value, int size) {
        final Collection<Person> result = new HashSet<Person>();
        for (final Person person : Person.findPerson(value, size)) {
            if (person.getUser() != null && (person.getTeacher() != null || RoleType.RESEARCHER.isMember(person.getUser()))) {
                result.add(person);
            }
        }
        return result;
    }
}
