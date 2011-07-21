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
  <r:require modules="jquery"/>
  <r:script>
    $(function() {
      $('#menu-item-sujets').addClass('actif');
    });
  </r:script>
  <title>TDBase - Edition du sujet</title>
</head>

<body>

<div class="column span-22 last middle">
  <h1>${titrePage}</h1>

  <form name="f_1_30_1" method="post" action="#">
    <div class="portal-form_container">
      <table>
        <tr>
          <td class="label">
            titre :
          </td>
          <td>
            <input size="80" type="text" value="Nouveau sujet"
                   name="1.30.1.1"/>
          </td>
        </tr>
      </table>

      <div id="allItemsContainer" updateUrl="#">
      </div><script>AUC.register('allItemsContainer');</script>

    </div>
    <div class="form_actions">
        <input type="submit" value="Exporter" name="1.30.1.5"/>
        <g:actionSubmit action="editeProprietes" value="Editer les propriétés du sujet"/>
        <input type="submit" value="Ajouter un élément" name="1.30.1.9"/>
        <input type="submit" value="Enregistrer" name="1.30.1.11"/>
      </div>
  </form>
</div>

</body>
</html>