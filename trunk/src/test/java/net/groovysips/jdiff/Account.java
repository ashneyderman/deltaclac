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

import java.math.BigDecimal;
import java.util.List;

/**
 * TODO: provide javadoc.
 *
 * @author Alex Shneyderman
 * @since 0.5
 */
public class Account
{

    private BigDecimal currentBalance;
    private String accountNumber;

    private List<String> aliases;

    public BigDecimal getCurrentBalance()
    {
        return currentBalance;
    }

    public void setCurrentBalance( BigDecimal currentBalance )
    {
        this.currentBalance = currentBalance;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public void setAccountNumber( String accountNumber )
    {
        this.accountNumber = accountNumber;
    }

    public List<String> getAliases()
    {
        return aliases;
    }

    public void setAliases( List<String> aliases )
    {
        this.aliases = aliases;
    }
}
