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
  <meta name="layout" content="eliot-tdbase-resultats"/>
  <r:require modules="jquery"/>
  <r:script>
    $(document).ready(function() {
      $('#menu-item-resultats').addClass('actif');
      $("select").change(function() {
        $("form").submit();
      })
    });
  </r:script>
  <title><g:message code="resultats.liste.head.title" /></title>
</head>

<body>

  <g:render template="/breadcrumps" plugin="eliot-tice-plugin" model="[liens: liens]"/>
  <div class="portal-form_container result">
    <g:form method="get" action="liste" controller="resultats" name="form-eleve-select">
           <table>
             <tr>
               <td class="label">Élève :</td>
               <td><strong><g:select from="${eleves}" name="eleveId"
                                     optionKey="id" optionValue="nomAffichage"
                                     value="${eleveSelectionne.id}"/></strong>
               </td>
             </tr>
           </table>
    </g:form>
  </div>
  <g:if test="${copies}">
    <div class="portal_pagination">
      <p class="nb_result">${copies.totalCount} résultat(s)</p> 
      <div class="pager"> Page(s) : <g:paginate total="${copies.totalCount}"></g:paginate></div>
    </div>
    
    <div class="portal-default_results-list sceance resultats">	
    	<g:each in="${copies}" status="i" var="copie">
    	  <div class="${(i % 2) == 0 ? 'even' : 'odd'}">
    	  	<g:set var="seance" value="${copie.modaliteActivite}"/>
    	  	<h1>${seance.matiere?.libelleLong}</h1>
    	  
    	  	<g:link action="visualiseCopie" controller="resultats" class="button voir"
    	  	        id="${copie.id}" title="Visualiser la copie">
    	  	</g:link>
    	  	
    	  	<h2>∟ <span>${seance.sujet.titre}</span></h2>
    	  	<p>
    	  		<em>(Scéance du :  ${seance.dateDebut.format('dd/MM/yy HH:mm')}  au ${seance.dateFin.format('dd/MM/yy HH:mm')})</em>
    	  	</p>
    	  	<p class="note">
    	  		<strong> » Note finale : </strong><b><g:formatNumber number="${copie.correctionNoteFinale}" format="##0.00" /> / <g:formatNumber number="${copie.maxPoints}" format="##0.00" /></b>
    	  		<strong> » Note auto. :</strong> <g:formatNumber number="${copie.correctionNoteAutomatique}" format="##0.00" /> / <g:formatNumber number="${copie.maxPointsAutomatique}" format="##0.00" />
    	  		<strong> » Note Prof. :</strong> <g:formatNumber number="${copie.correctionNoteCorrecteur}" format="##0.00" /> / <g:formatNumber number="${copie.maxPointsCorrecteur}" format="##0.00" />
    	  	</p>
    	  	
    	  </div>
   		</g:each>
   	</div>

    
  </g:if>
  <g:else>
     <div class="portal_pagination">
      <p class="nb_result">Aucune copie à visualiser</p>
    </div>
  </g:else>


</body>
</html>