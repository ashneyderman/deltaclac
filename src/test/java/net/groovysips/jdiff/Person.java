/* Copyright 2009 Alex Shneyderman
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package net.groovysips.jdiff;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.util.ObjectUtils;


/**
 * @author Alex Shneyderman
 * @since 0.3
 */
public class Person
{
    private String name;
    private String ssn;
    private Date dob;
    private int yearsInSchool;
    private Collection<Person> children;

    private List<Account> accounts;

    private Person spouse;

    private Person firstChild;

    public Collection<Person> getChildren()
    {
        return children;
    }

    public void setChildren( Collection<Person> children )
    {
        this.children = children;
    }

    public Date getDob()
    {
        return dob;
    }

    public void setDob( Date dob )
    {
        this.dob = dob;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getSsn()
    {
        return ssn;
    }

    public void setSsn( String ssn )
    {
        this.ssn = ssn;
    }

    public int getYearsInSchool()
    {
        return yearsInSchool;
    }

    public void setYearsInSchool( int yearsInSchool )
    {
        this.yearsInSchool = yearsInSchool;
    }

    public Person getSpouse()
    {
        return spouse;
    }

    public void setSpouse( Person spouse )
    {
        this.spouse = spouse;
    }

    public Person getFirstChild()
    {
        return firstChild;
    }

    public void setFirstChild( Person firstChild )
    {
        this.firstChild = firstChild;
    }

    public List<Account> getAccounts()
    {
        return accounts;
    }

    public void setAccounts( List<Account> accounts )
    {
        this.accounts = accounts;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj instanceof Person )
        {
            Person other = (Person) obj;
            return same( this.name, other.getName() )
                   && same( this.dob, other.getDob() )
                   && same( this.ssn, other.getSsn() )
                   && ( this.yearsInSchool == other.getYearsInSchool() )
                   && ( ObjectUtils.nullSafeEquals( this.getSpouse(), other.getSpouse() ) );
        }
        return false;
    }

    @Override public String toString()
    {
        return "Person{" +
               "children=" + children +
               ", name='" + name + '\'' +
               ", ssn='" + ssn + '\'' +
               ", dob=" + dob +
               ", yearsInSchool=" + yearsInSchool +
               ", spouse=" + ( spouse == null ? "null" : spouse.getName() ) +
               ", firstChild=" + firstChild +
               '}';
    }

    private boolean same( Object right, Object left )
    {
        if( right == null && left == null )
        {
            return true;
        }
        if( right == null || left == null )
        {
            return false;
        }
        return right.equals( left );
    }

}