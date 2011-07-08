/*
 * Copyright © FYLAB and the Conseil Régional d'Île-de-France, 2009
 * This file is part of L'Interface Libre et Interactive de l'Enseignement (Lilie).
 *
 * Lilie is free software. You can redistribute it and/or modify since
 * you respect the terms of either (at least one of the both license) :
 * - under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * - the CeCILL-C as published by CeCILL-C; either version 1 of the
 * License, or any later version
 *
 * There are special exceptions to the terms and conditions of the
 * licenses as they are applied to this software. View the full text of
 * the exception in file LICENSE.txt in the directory of this software
 * distribution.
 *
 * Lilie is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Licenses for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the CeCILL-C along with Lilie. If not, see :
 *  <http://www.gnu.org/licenses/> and
 *  <http://www.cecill.info/licences.fr.html>.
 */

package org.lilie.services.eliot.tice.textes;
/**
 * The ContexteActivite entity.
 *
 * @author
 *
 *
 */
class ContexteActivite {
  static mapping = {
    table 'entcdt.contexte_activite'
    id column:'id', generator: 'sequence', params: [sequence: 'entcdt.contexte_activite_id_seq']
    cache true
    version false
  }
  Long id  
  String code
  String nom
  String description

  public static final String CODE_MAISON = 'MAI'
  public static final String CODE_CLASSE = 'CLA'
  
  static constraints = {
    id(max: 9999999999L)
    version(max: 9999999999L)
    code(size: 1..5, blank: false)
    nom(size: 1..255, blank: false)
  }

  String toString() {
    return "${id}"
  }

}