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
  <r:require modules="eliot-tdbase-ui"/>
  <r:script>
    $(document).ready(function () {
      $('#menu-item-seances').addClass('actif');
      $('.supprime').click(function () {
        return confirm('Êtes vous sur de vouloir supprimer la séance et toutes les copies associées ?');
      });
      initButtons();
    });
  </r:script>
  <title>TDBase - Liste des séances</title>
</head>

<body>
<g:render template="/breadcrumps" plugin="eliot-tice-plugin"
          model="[liens: liens]"/>

<g:if test="${seances}">
  <div class="portal_pagination">
    <p class="nb_result">${seances.totalCount} résultat(s)</p> <g:paginate
            total="${seances.totalCount}"></g:paginate>
  </div>

	<div class="portal-default_results-list sceance">
		<g:each in="${seances}" status="i" var="seance">
		  <div class="${(i % 2) == 0 ? 'even' : 'odd'}">
		  	<h1> ${seance.sujet.titre}</h1>
		  	
		  	<button id="${seance.id}">Actions</button><ul id="menu_actions_${seance.id}"
		  	    class="tdbase-menu-actions">
		  	  <li><g:link action="listeResultats" controller="seance"
		  	              id="${seance.id}">
		  	    Corriger les copies
		  	  </g:link>
		  	  </li>
		  	  <li><hr/></li>
		  	  <li><g:link action="edite" controller="seance"
		  	                      id="${seance.id}">
		  	                Modifier
		  	              </g:link>
		  	  </li>
		  	  <li><g:link action="supprime" controller="seance"
		  	                                  id="${seance.id}" class="supprime">
		  	      Supprimer
		  	     </g:link>
		  	  </li>
		  	</ul>
		  	<p><strong> » Groupe : </strong><b>${seance.groupeLibelle}</b></p>
		  	<p>
		  		
		  		<g:if test="${seance.matiere?.libelleLong}"><strong>» Matière : </strong>${seance.matiere?.libelleLong}</g:if>
		  		<strong> » Début de la séance : </strong>${seance.dateDebut.format('dd/MM/yy HH:mm')} 
		  		<strong> » Fin : </strong>${seance.dateFin.format('dd/MM/yy HH:mm')}
		  	</p>
		  	
		  </div>
		</g:each> 
	</div>
	
</g:if>
<g:else>
  <div class="portal_pagination">
    Aucune séance
  </div>
</g:else>

</body>
</html>