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




<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="layout" content="eliot-tdbase-activite"/>
  <r:require modules="core"/>
  <r:script>
    $(document).ready(function() {
      $('#menu-item-seances').addClass('actif');

    });
  </r:script>
  <title>TDBase - Edition d'une copie</title>
</head>

<body>

<div class="column span-22 last middle">
  <g:render template="/breadcrumps" model="[liens: liens]"/>

  <g:hasErrors bean="${copie}">
    <div class="portal-messages error">
      <g:eachError>
        <li><g:message error="${it}"/></li>
      </g:eachError>
    </div>
  </g:hasErrors>
  <g:if test="${request.messageCode}">
    <div class="portal-messages success">
      <li><g:message code="${request.messageCode}"
                     class="portal-messages success"/></li>
    </div>
  </g:if>
  <g:set var="sujet" value="${copie.sujet}"/>
  <form method="post">
    <g:if test="${!copie.estModifiable()}">
      <div class="portal-messages notice">
        <span class="portal-messages notice">La copie n'est plus modifiable.</span>
      </div>
    </g:if>
    <div style="text-align: right;">
      <g:link action="${lienRetour.action}"
              controller="${lienRetour.controller}"
              params="${lienRetour.params}">Annuler</g:link>&nbsp;
      <g:if test="${copie.estModifiable()}">|&nbsp;
        <g:actionSubmit value="Rendre la copie" action="rendLaCopie"
                      title="Rendre la copie" />
      </g:if>
    </div>
    <g:hiddenField name="copie.id" value="${copie.id}"/>
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
                 style="float: left">
          ${reponse.correctionNoteAutomatique}&nbsp;/&nbsp;${sujetQuestion.points}
            </div>
            &nbsp;point(s)</div>
        </g:if>
        <g:set var="question" value="${sujetQuestion.question}"/>

        <g:if test="${question.type.interaction}">
          <div class="tdbase-sujet-edition-question-interaction">

            <g:hiddenField name="reponsesCopie.listeReponses[${indexReponse}].reponse.id" value="${reponse.id}"/>

            <g:render
                    template="/question/${question.type.code}/${question.type.code}Interaction"
                    model="[question:question, reponse:reponse, indexReponse:indexReponse++]"/>
          </div>
        </g:if>
        <g:else>
          <div class="tdbase-sujet-edition-question-interaction">
            <g:set var="question" value="${sujetQuestion.question}"/>
            <g:render
                    template="/question/${question.type.code}/${question.type.code}Preview"
                    model="[question:question]"/>
          </div>
        </g:else>

      </div>

    </g:each>
    <g:hiddenField name="nombreReponses" value="${indexReponse}"/>
    <div style="text-align: right;margin-top: 20px;">
      <g:link action="${lienRetour.action}"
              controller="${lienRetour.controller}"
              params="${lienRetour.params}">Annuler</g:link>&nbsp;
      <g:if test="${copie.estModifiable()}">|&nbsp;
        <g:actionSubmit value="Rendre la copie" action="rendLaCopie"
                      title="Rendre la copie" />
      </g:if>
    </div>
  </form>
</div>

</body>
</html>