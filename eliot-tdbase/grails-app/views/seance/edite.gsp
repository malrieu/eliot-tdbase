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
  <meta name="layout" content="eliot-tdbase"/>
  <r:require module="eliot-tice-ui"/>
  <r:script>
    $(document).ready(function () {
      $('#menu-item-seances').addClass('actif');
      $('select[name="proprietesScolariteSelectionId"]').focus();
      $(".datepicker").datetimepicker();
      var $confirmDialog = $("<div></div>")
      							.html('Êtes vous sur de vouloir supprimer la séance avec toutes les copies associées ?')
      							.dialog({
      								autoOpen: false,
      								title: "Suppression de la séance",
      								modal: true,
      								buttons : {
      									"Annuler": function() {$(this).dialog("close");return false},
      									'OK': function() {
                                            $(this).dialog("close");
                                            document.location = "${createLink(action: 'supprime', controller: 'seance', id: modaliteActivite.id)}";
                                            }
      								}
      							});
      $('.delete-actif').click(function () {
        $confirmDialog.dialog('open');
        return false;
      });
    $('#gestionEvaluation').change(function() {
               $('#edition_devoir').toggle();
               })
    $('#gestionActivite').change(function() {
                   $('#edition_activite').toggle();
                   })
    });
  </r:script>
  <title><g:message code="seance.edite.head.title"/></title>
</head>

<body>
<g:render template="/breadcrumps" plugin="eliot-tice-plugin"
          model="[liens: liens]"/>
<div class="portal-tabs">
  <span class="portal-tabs-famille-liens">
    <g:if test="${modaliteActivite.id}">
      <g:link action="listeResultats" controller="seance" class="modify"
              id="${modaliteActivite.id}">Corriger les copies</g:link> |
      <g:link action="supprime" controller="seance" class="delete delete-actif"
              id="${modaliteActivite.id}">Supprimer la séance</g:link>
    </g:if>
    <g:else>
      <span class="modify">Corriger les copies</span> |
      <span class="delete">Supprimer la séance</span>
    </g:else>
  </span>
</div>

<g:hasErrors bean="${modaliteActivite}">
  <div class="portal-messages">
    <g:eachError>
      <li class="error"><g:message error="${it}"/></li>
    </g:eachError>
  </div>
</g:hasErrors>
<g:if test="${flash.messageCode}">
  <div class="portal-messages">
    <li class="success"><g:message code="${flash.messageCode}"
                                   class="portal-messages success"/></li>
  </div>
</g:if>


<g:form method="post" controller="seance" action="edite">
  <div class="portal-form_container edite">
    <table>

      <tr>
        <td class="label">Groupe&nbsp;:</td>
        <td>
          <g:if test="${modaliteActivite.structureEnseignement}">
            <strong>${modaliteActivite.structureEnseignement.nomAffichage}</strong> &nbsp;&nbsp;&nbsp;
            <g:select name="proprietesScolariteSelectionId"
                      noSelection="${['null': 'Changer de groupe...']}"
                      from="${proprietesScolarite}"
                      optionKey="id"
                      optionValue="structureEnseignementNomAffichage"/>
          </g:if>
          <g:else>
            <g:select name="proprietesScolariteSelectionId"
                      noSelection="${['null': g.message(code: "default.select.null")]}"
                      from="${proprietesScolarite}"
                      optionKey="id"
                      optionValue="structureEnseignementNomAffichage"/>
          </g:else>
        </td>
      </tr>
      <tr>
        <td class="label">Sujet&nbsp;:</td>
        <td>
          <strong>${modaliteActivite.sujet.titre}</strong> <br/>
        </td>
      </tr>
      <tr>
        <td class="label">Début&nbsp;:</td>
        <td>
          <g:textField name="dateDebut"
                       value="${modaliteActivite.dateDebut.format('dd/MM/yyyy HH:mm')}"
                       class="datepicker"/>
        </td>
      </tr>
      <tr>
        <td class="label">Fin&nbsp;:</td>
        <td>
          <g:textField name="dateFin"
                       value="${modaliteActivite.dateFin.format('dd/MM/yyyy HH:mm')}"
                       class="datepicker"/>
        </td>
      </tr>
      <tr>
        <td class="label"></td>
        <td>
          <g:checkBox name="copieAmeliorable" title="améliorable"
                      checked="${modaliteActivite.copieAmeliorable}"/> <span
                class="label">Copie&nbsp;améliorable</span>
        </td>
      </tr>
      <g:if test="${grailsApplication.config.eliot.interfacage.notes}">
        <tr>
          <td class="label"></td>
          <td>
            <g:checkBox name="gestionEvaluation" id="gestionEvaluation"
                        title="Liée à un devoir dans la gestion des notes"
                        checked="${modaliteActivite.evaluationId}"/>
            <span class="label">Liée à un devoir dans la gestion des notes</span>
            <table id="edition_devoir"
                   style="display: ${modaliteActivite.evaluationId ? 'table' : 'none'}">
              <tr>
                <td class="label" style="width: 110px;">Matières&nbsp;:</td>
                <td><g:select name="matiereId"
                              noSelection="${['null': 'Faites votre choix...']}"
                              from="${proprietesScolarite}"
                              optionKey="id"
                              optionValue="matiere"/></td>
              </tr>

            </table>
          </td>
        </tr>
      </g:if>
      <g:if test="${grailsApplication.config.eliot.interfacage.textes}">
        <tr>
          <td class="label"></td>
          <td>
            <g:checkBox name="gestionActivite"
                        title="Liée à une activité dans le cahier de textes"
                        checked="${modaliteActivite.activiteId}"/>
            <span class="label">Liée à une activité dans le cahier de textes</span>
            <table id="edition_activite"
                   style="display: ${modaliteActivite.activiteId ? 'table' : 'none'}">
              <tr>
                <td class="label"
                    style="width: 110px;">Cahier&nbsp;de&nbsp;textes&nbsp;:</td>
                <td><g:select name="cahierId"
                              noSelection="${['null': 'Faites votre choix...']}"
                              from="${proprietesScolarite}"
                              optionKey="id"
                              optionValue="matiere"/></td>
              </tr>
              <tr>
                <td class="label" style="width: 110px;">Chapitre&nbsp;:</td>
                <td><g:select name="chapitreId"
                              noSelection="${['null': 'Faites votre choix...']}"
                              from="${proprietesScolarite}"
                              optionKey="id"
                              optionValue="matiere"/></td>
              </tr>
              <tr>
                <td class="label" style="width: 110px;">Contexte&nbsp;:</td>
                <td><g:select name="chapitreId"
                              noSelection="${['null': 'En classe']}"
                              from="${proprietesScolarite}"
                              optionKey="id"
                              optionValue="matiere"/></td>
              </tr>
            </table>
          </td>
        </tr>
      </g:if>
    </table>
  </div>
  <g:hiddenField name="id" value="${modaliteActivite.id}"/>
  <g:hiddenField id="sujetId" name="sujet.id"
                 value="${modaliteActivite.sujet?.id}"/>
  <div class="form_actions edite">
    <g:actionSubmit value="Enregistrer" class="button"
                    action="enregistre"
                    title="Enregistrer"/>
  </div>
  <br/><br/><br/><br/><br/>
</g:form>

</body>
</html>