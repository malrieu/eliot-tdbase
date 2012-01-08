%{--
  - Copyright © FYLAB and the Conseil Régional d'Île-de-France, 2009
  - This file is part of L'Interface Libre et Interactive de l'Enseignement (Lilie).
  -
  - Lilie is free software. You can redistribute it and/or modify since
  - you respect the terms of either (at least one of the both license) :
  -  under the terms of the GNU Affero General Public License as
  - published by the Free Software Foundation, either version 3 of the
  - License, or (at your option) any later version.
  -  the CeCILL-C as published by CeCILL-C; either version 1 of the
  - License, or any later version
  -
  - There are special exceptions to the terms and conditions of the
  - licenses as they are applied to this software. View the full text of
  - the exception in file LICENSE.txt in the directory of this software
  - distribution.
  -
  - Lilie is distributed in the hope that it will be useful,
  - but WITHOUT ANY WARRANTY; without even the implied warranty of
  - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  - Licenses for more details.
  -
  - You should have received a copy of the GNU General Public License
  - and the CeCILL-C along with Lilie. If not, see :
  -  <http://www.gnu.org/licenses/> and
  -  <http://www.cecill.info/licences.fr.html>.
  --}%

<%@ page import="org.lilie.services.eliot.tice.utils.NumberUtils" %>


<g:set var="sujet" value="${copie.sujet}"/>
<div class="portal-form_container">
  <table>
    <tr>
      <td class="label">Élève :</td>
      <td><strong>${copie.eleve.nomAffichage}</strong></td>
    </tr>

    <tr>
      <td class="label">Appréciation :</td>
      <td>
        ${copie.correctionAnnotation}
      </td>
    </tr>
    <tr>
      <td class="label">Modulation :</td>
      <td>
        ${NumberUtils.formatFloat(copie.pointsModulation)}
      </td>
    </tr>
    <tr>
      <td class="label">Note :</td>
      <td>
        <strong>${NumberUtils.formatFloat(copie.correctionNoteFinale)}
          / ${NumberUtils.formatFloat(copie.maxPoints)}</strong>
      </td>
    </tr>
  </table>
</div>
<g:if test="${copie.modaliteActivite.estOuverte()}">
  <g:if test="${copie.dateRemise}">
    <div class="portal-messages notice">
      Note (correction automatique) :
      <g:formatNumber number="${copie.correctionNoteAutomatique}"
                      format="##0.00"/>
      / <g:formatNumber number="${copie.maxPoints}" format="##0.00"/>
      &nbsp;&nbsp;(copie remise le ${copie.dateRemise.format('dd/MM/yy  à HH:mm')})
    </div>
  </g:if>
  <g:if test="${!copie.estModifiable()}">
    <div class="portal-messages notice">
      La copie n'est plus modifiable.
    </div>
  </g:if>
</g:if>
<form method="post">

  <h3 class="tdbase-sujet-titre">${sujet.titre}</h3>
  <g:set var="indexReponse" value="0"/>
  <g:each in="${sujet.questionsSequences}" var="sujetQuestion">
    <div class="tdbase-sujet-edition-question">
      <g:if test="${sujetQuestion.question.type.interaction}">
        <g:set var="reponse"
               value="${copie.getReponseForSujetQuestion(sujetQuestion)}"/>

        <div class="tdbase-sujet-edition-question-points"
             style="margin-bottom: 15px">
          <div id="SujetSequenceQuestions-${sujetQuestion.id}"
               style="float: left;width: 60px;">
            <g:if test="${reponse}">
              <g:if test="${reponse.estEnNotationManuelle()}">
                <g:formatNumber number="${reponse.correctionNoteCorrecteur}"
                                format="##0.00"/>
              </g:if>
              <g:else>
                <g:formatNumber number="${reponse.correctionNoteAutomatique}"
                                format="##0.00"/>
              </g:else>
            </g:if>
            <g:else>
              <span title="Copie rendue après ajout de cette question.">Non&nbsp;notée&nbsp;</span>
            </g:else>
          </div>
          &nbsp;/&nbsp;<g:formatNumber number="${sujetQuestion.points}"
                                       format="##0.00"/>&nbsp;point(s)
        </div>
      </g:if>
      <g:set var="question" value="${sujetQuestion.question}"/>
      <div class="tdbase-sujet-edition-question-interaction">
        <g:if test="${question.type.interaction}">

          <g:render
                  template="/question/${question.type.code}/${question.type.code}Interaction"
                  model="[question: question, reponse: reponse, indexReponse: indexReponse++]"/>

          <g:if test="${copie.modaliteActivite.estPerimee()}">
            <g:render
                    template="/question/${question.type.code}/${question.type.code}Correction"
                    model="[question: question]"/>
          </g:if>

        </g:if>
        <g:else>

          <g:render
                  template="/question/${question.type.code}/${question.type.code}Preview"
                  model="[question: question]"/>

        </g:else>
      </div>
    </div>

  </g:each>

</form>

