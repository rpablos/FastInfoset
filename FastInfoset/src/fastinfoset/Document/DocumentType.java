/*
* Copyright 2014 Ronald Pablos.
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

package fastinfoset.Document;

import java.util.List;

/**
 *
 * @author rpablos
 */
public class DocumentType {
    public String systemIdentifier;
    public String publicIdentifier;
    public List<ProcessingInstruction> instructions;

    public DocumentType(String systemIdentifier, String publicIdentifier, List<ProcessingInstruction> instructions) {
        this.systemIdentifier = systemIdentifier;
        this.publicIdentifier = publicIdentifier;
        this.instructions = instructions;
    }
    
}
