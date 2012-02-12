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








package org.lilie.services.eliot.tdbase.impl

import org.lilie.services.eliot.tdbase.QuestionController
import org.lilie.services.eliot.tdbase.impl.fileupload.FileUploadSpecification
import org.lilie.services.eliot.tdbase.impl.fileupload.ReponseFileUploadSpecification

class QuestionFileUploadController extends QuestionController {

  /**
     * Action "supprimeAttachement"
     */
    def supprimeAttachement() {
      FileUploadSpecification spec = getSpecificationObjectFromParams(params)
      spec.questionAttachementId = null
      spec.fichier = null
      render(template: "/question/FileUpload/FileUploadEditionFichier", model: [specifobject: spec])
    }

  /**
       * Action "supprimeReponseAttachement"
       */
      def supprimeReponseAttachement() {
        ReponseFileUploadSpecification spec = getReponseSpecificationObjectFromParams(params)
        spec.reponseAttachementId = null
        spec.fichier = null
        // todofsil : recuperer l'index en récupérant la valeur de l'update
        render(template: "/question/FileUpload/FileUploadInteractionFichier", model: [specifobject: spec])
      }

  /**
   *
   * @param params les paramètres de la requête
   * @return l'objet représentant la spécification
   */
  def getSpecificationObjectFromParams(Map params) {
    def specifobject = new FileUploadSpecification()
    bindData(specifobject, params, "specifobject")
    return specifobject
  }

  /**
     *
     * @param params les paramètres de la requête
     * @return l'objet représentant la spécification
     */
    def getReponseSpecificationObjectFromParams(Map params) {
      def specifobject = new ReponseFileUploadSpecification()
      bindData(specifobject, params, "specifobject")
      return specifobject
    }
}

