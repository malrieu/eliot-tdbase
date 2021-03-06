import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.lilie.services.eliot.tice.AttachementDataStore
import org.lilie.services.eliot.tice.annuaire.Personne
import org.lilie.services.eliot.tice.annuaire.impl.DefaultRoleUtilisateurService
import org.lilie.services.eliot.tice.annuaire.impl.DefaultUtilisateurService
import org.lilie.services.eliot.tice.annuaire.impl.LilieRoleUtilisateurService
import org.lilie.services.eliot.tice.annuaire.impl.LilieUtilisateurService
import org.lilie.services.eliot.tice.securite.CompteUtilisateur
import org.lilie.services.eliot.tice.securite.DomainAutorite
import org.lilie.services.eliot.tice.utils.EliotApplicationEnum
import org.lilie.services.eliot.tice.utils.EliotUrlProvider
import org.lilie.services.eliot.tice.utils.UrlServeurResolutionEnum

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

class EliotTicePluginGrailsPlugin {
  // the group id
  def groupId = "org.lilie.services.eliot"
  // the plugin version
  def version = "2.0.0-SNAPSHOT"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "2.0.1 > *"
  // the other plugins this plugin depends on
  def dependsOn = [:]
  // resources that are excluded from plugin packaging
  def pluginExcludes = ["grails-app/views/error.gsp"]

  def title = "Eliot Tice Plugin"
  def author = "Franck Silvestre"
  def authorEmail = ""
  def description = '''\
      Plugin pour la création d'applications Eliot
      '''

  def doWithSpring = {

    // configure la gestion de l'annuaire
    //
    if (ConfigurationHolder.config.eliot.portail.lilie) {

      utilisateurService(LilieUtilisateurService) {
        springSecurityService = ref("springSecurityService")
      }

      roleUtilisateurService(LilieRoleUtilisateurService) {
        profilScolariteService = ref("profilScolariteService")
      }

    } else {

      utilisateurService(DefaultUtilisateurService) {
        springSecurityService = ref("springSecurityService")
      }

      roleUtilisateurService(DefaultRoleUtilisateurService) {
        profilScolariteService = ref("profilScolariteService")
      }
    }

    // Configure la gestion du datastore
    //
    dataStore(AttachementDataStore) { bean ->
      path = ConfigurationHolder.config.eliot.fichiers.racine ?: null
      bean.initMethod = 'initFileDataStore'
    }

    // configure la gestion d'EliotUrlProvider
    //
    def conf = ConfigurationHolder.config

    String reqHeaderPorteur = conf.eliot.requestHeaderPorteur ?: null
    if (!reqHeaderPorteur) {
      def message = """
                    Le paramètre eliot.requestHeaderPorteur n'a pas été configuré
                    """
      throw new IllegalStateException(message)
    }

    EliotApplicationEnum applicationEnum = conf.eliot.eliotApplicationEnum ?: null
    if (!applicationEnum) {
      def message = """
              Le paramètre eliot.eliotApplicationEnum n'a pas été configuré
              """
      throw new IllegalStateException(message)
    }

    String nomAppl = conf.eliot."${applicationEnum.code}".nomApplication ?: null
    if (!nomAppl) {
      def message = """
              Le paramètre eliot.${applicationEnum.code}.nomApplication n'a pas été configuré
              """
      throw new IllegalStateException(message)
    }

    String urlResolutionMode = conf.eliot.urlResolution.mode ?: null
    if (!urlResolutionMode) {
      def message = """
              Le paramètre eliot.urlResolution.mode n'a pas été configuré
              """
      throw new IllegalStateException(message)
    }
    UrlServeurResolutionEnum urlServeurResolEnum = UrlServeurResolutionEnum.valueOf(UrlServeurResolutionEnum.class,
                                                                                    urlResolutionMode.toUpperCase())
    String urlServeurFromConfig = null
    if (urlServeurResolEnum == UrlServeurResolutionEnum.CONFIGURATION) {
      if (conf.eliot."${applicationEnum.code}".urlServeur) {
        urlServeurFromConfig = conf.eliot."${applicationEnum.code}".urlServeur
      }
      if (conf.eliot.commons?.urlServeur) {
        urlServeurFromConfig = conf.eliot.commons.urlServeur
      }
    }

    if (urlServeurResolEnum == UrlServeurResolutionEnum.CONFIGURATION && !urlServeurFromConfig) {
      def message = """
      L'url serveur de l'application ${applicationEnum.code} n'a pas " + "été configurée
      """
      throw new IllegalStateException(message)
    }

    eliotUrlProvider(EliotUrlProvider) { bean ->
      requestHeaderPorteur = reqHeaderPorteur
      nomApplication = nomAppl
      urlServeurResolutionEnum = urlServeurResolEnum
      urlServeurFromConfiguration = urlServeurFromConfig
    }

  }

  def doWithDynamicMethods = { ctx ->
    for (controllerClass in application.controllerClasses) {
      addControllerMethods controllerClass.metaClass, ctx
    }
  }

  private void addControllerMethods(MetaClass mc, ctx) {
    if (!mc.respondsTo(null, 'getAuthenticatedUser')) {
      mc.getAuthenticatedUser = {->
        if (!ctx.springSecurityService.isLoggedIn()) return null
        Personne.get(ctx.springSecurityService.principal.personneId)
      }
    }

    if (!mc.respondsTo(null, 'getAuthenticatedPersonne')) {
      mc.getAuthenticatedPersonne = {->
        if (!ctx.springSecurityService.isLoggedIn()) return null
        Personne.get(ctx.springSecurityService.principal.personneId)
      }
    }

    if (!mc.respondsTo(null, 'getAuthenticatedCompteUtilisateur')) {
      mc.getAuthenticatedCompteUtilisateur = {->
        if (!ctx.springSecurityService.isLoggedIn()) return null
        CompteUtilisateur.get(ctx.springSecurityService.principal.compteUtilisateurId)
      }
    }

    if (!mc.respondsTo(null, 'getAuthenticatedAutorite')) {
      mc.getAuthenticatedAutorite = {->
        if (!ctx.springSecurityService.isLoggedIn()) return null
        DomainAutorite.get(ctx.springSecurityService.principal.autoriteId)
      }
    }
  }
}
