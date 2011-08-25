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



package org.lilie.services.eliot.tdbase

import org.gcontracts.annotations.Requires
import org.lilie.services.eliot.tice.annuaire.Personne
import org.springframework.transaction.annotation.Transactional

/**
 * Service de gestion des questions
 * @author franck silvestre
 */
class ModaliteActiviteService {

  static transactional = false

  /**
   * Créé une séance (modaliteActivite)
   * @param proprietes les propriétés
   * @param proprietaire le proprietaire
   * @return la séance créée
   */
  @Transactional
  ModaliteActivite createModaliteActivite(Map proprietes, Personne proprietaire) {
    ModaliteActivite modaliteActivite = new ModaliteActivite(
            enseignant: proprietaire
    )
    modaliteActivite.properties = proprietes
    modaliteActivite.save()
    return modaliteActivite
  }

  /**
   * Modifie les proprietes de la question passée en paramètre
   * @param modaliteActivite la séance
   * @param proprietes les nouvelles proprietes
   * @param proprietaire le proprietaire
   * @return la séance modifiée
   */
  @Transactional
  @Requires({modaliteActivite.enseignant == proprietaire})
  ModaliteActivite updateProprietes(ModaliteActivite modaliteActivite, Map proprietes,
                                    Personne proprietaire) {


    modaliteActivite.properties = proprietes
    modaliteActivite.save()
    return modaliteActivite
  }

  /**
   * Recherche de séance
   * @param chercheur la personne effectuant la recherche
   * @param paginationAndSortingSpec les specifications pour l'ordre et
   * la pagination
   * @return la liste des séance
   */
  @Requires({chercheur != null})
  List<ModaliteActivite> findModalitesActivites(Personne chercheur,
                                                Map paginationAndSortingSpec = null) {
    if (!chercheur) {
      throw new IllegalArgumentException("question.recherche.chercheur.null")
    }
    if (paginationAndSortingSpec == null) {
      paginationAndSortingSpec = [:]
    }

    def criteria = ModaliteActivite.createCriteria()
    List<ModaliteActivite> seances = criteria.list(paginationAndSortingSpec) {
      eq 'enseignant', chercheur

      if (paginationAndSortingSpec) {
        def sortArg = paginationAndSortingSpec['sort'] ?: 'dateDebut'
        def orderArg = paginationAndSortingSpec['order'] ?: 'desc'
        if (sortArg) {
          order "${sortArg}", orderArg
        }

      }
    }
    return seances
  }


}

