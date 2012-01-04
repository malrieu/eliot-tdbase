package org.lilie.services.eliot.tdbase

import grails.converters.JSON
import grails.plugins.springsecurity.SpringSecurityService
import org.lilie.services.eliot.tice.annuaire.Personne
import org.lilie.services.eliot.tice.scolarite.Matiere
import org.lilie.services.eliot.tice.scolarite.Niveau
import org.lilie.services.eliot.tice.scolarite.ProfilScolariteService
import org.lilie.services.eliot.tice.utils.Breadcrumps
import org.lilie.services.eliot.tice.utils.BreadcrumpsService
import org.lilie.services.eliot.tice.utils.NumberUtils

class SujetController {

  static defaultAction = "mesSujets"
  private static final String PARAM_DIRECTION_AVANT = 'avant'
  static final String PROP_RANG_INSERTION = 'rangInsertion'

  SujetService sujetService
  QuestionService questionService
  CopieService copieService
  ReponseService reponseService
  SpringSecurityService springSecurityService
  ProfilScolariteService profilScolariteService
  BreadcrumpsService breadcrumpsService
  ArtefactAutorisationService artefactAutorisationService

  /**
   *
   * Action "recherche"
   */
  def recherche(RechercheSujetCommand rechCmd) {
    params.max = Math.min(params.max ? params.int('max') : 10, 100)
    if (params.term) { // compatibilite avec autocomplete jquery
      rechCmd.patternTitre = params.term
    } else {
      breadcrumpsService.manageBreadcrumps(params, message(code: "sujet.recherche.titre"))
    }
    Personne personne = authenticatedPersonne
    def sujets = sujetService.findSujets(
            personne,
            rechCmd.patternTitre,
            rechCmd.patternAuteur,
            rechCmd.patternPresentation,
            Matiere.get(rechCmd.matiereId),
            Niveau.get(rechCmd.niveauId),
            SujetType.get(rechCmd.typeId),
            params
    )
    withFormat {
      html {
        [
                liens: breadcrumpsService.liens,
                afficheFormulaire: true,
                typesSujet: sujetService.getAllSujetTypes(),
                matieres: profilScolariteService.findMatieresForPersonne(personne),
                niveaux: profilScolariteService.findNiveauxForPersonne(personne),
                sujets: sujets,
                rechercheCommand: rechCmd,
                artefactHelper: artefactAutorisationService,
                utilisateur: personne
        ]
      }
      js {
        render sujets.collect { [id: it.id, value: it.titre] as JSON }
      }
    }
  }

  /**
   *
   * Action "mesSujets"
   */
  def mesSujets() {
    params.max = Math.min(params.max ? params.int('max') : 10, 100)
    breadcrumpsService.manageBreadcrumps(params, message(code: "sujet.messujets.titre"))
    Personne personne = authenticatedPersonne
    def model = [
            liens: breadcrumpsService.liens,
            afficheFormulaire: false,
            sujets: sujetService.findSujetsForProprietaire(
                    personne,
                    params),
            artefactHelper: artefactAutorisationService,
            utilisateur: personne
    ]
    render(view: "recherche", model: model)
  }

  /**
   *
   * Action "nouveau"
   */
  def nouveau() {
    breadcrumpsService.manageBreadcrumps(params, message(code: "sujet.nouveau.titre"))
    render(view: "edite", model: [
            liens: breadcrumpsService.liens,
            titreSujet: message(code: "sujet.nouveau.titre"),
            peutSupprimerSujet: false,
            peutPartagerSujet: false
    ])
  }

  /**
   *
   * Action "edite"
   */
  def edite() {
    breadcrumpsService.manageBreadcrumps(params, message(code: "sujet.edite.titre"))
    Personne personne = authenticatedPersonne
    Sujet sujet = Sujet.get(params.id)
    [
            liens: breadcrumpsService.liens,
            titreSujet: sujet.titre,
            sujet: sujet,
            sujetEnEdition: true,
            peutSupprimerSujet: artefactAutorisationService.utilisateurPeutSupprimerArtefact(personne, sujet),
            peutPartagerSujet: artefactAutorisationService.utilisateurPeutPartageArtefact(personne, sujet),
            artefactHelper: artefactAutorisationService,
            utilisateur: personne
    ]
  }

  /**
   *
   * Action "Dupliquer"
   */
  def duplique() {
    Personne personne = authenticatedPersonne
    Sujet sujet = Sujet.get(params.id)
    Sujet nveauSujet = sujetService.recopieSujet(sujet, personne)
    redirect(action: 'edite', id: nveauSujet.id)
  }

  /**
   * Action "Partager"
   */
  def partage() {
    Personne personne = authenticatedPersonne
    Sujet sujet = Sujet.get(params.id)
    if (!sujet.estPartage()) {
      sujetService.partageSujet(sujet, personne)
    }

    if (!sujet.hasErrors()) {
      request.messageCode = "sujet.partage.succes"
      request.messageArgs = [sujet.copyrightsType.presentation]
    }
    render(view: '/sujet/edite', model: [
            liens: breadcrumpsService.liens,
            titreSujet: sujet.titre,
            sujet: sujet,
            sujetEnEdition: true,
            peutSupprimerSujet: artefactAutorisationService.utilisateurPeutSupprimerArtefact(personne, sujet),
            peutPartagerSujet: artefactAutorisationService.utilisateurPeutPartageArtefact(personne, sujet),
            artefactHelper: artefactAutorisationService,
            utilisateur: personne
    ])

  }

  /**
   * Action "Supprimer"
   */
  def supprime() {
    Personne personne = authenticatedPersonne
    Sujet sujet = Sujet.get(params.id)
    sujetService.supprimeSujet(sujet, personne)
    def lien = breadcrumpsService.lienRetour()
    forward(action: lien.action,
            controller: lien.controller,
            params: lien.params)

  }

  /**
   * Action "teste"
   */
  def teste() {
    breadcrumpsService.manageBreadcrumps(params, message(code: "sujet.teste.titre"))
    Personne personne = authenticatedPersonne
    Sujet sujet = Sujet.get(params.id)
    Copie copie = copieService.getCopieTestForSujetAndPersonne(sujet, personne)
    [
            liens: breadcrumpsService.liens,
            lienRetour: breadcrumpsService.lienRetour(),
            copie: copie,
            afficheCorrection: false
    ]
  }

  /**
   *
   * Action rend la copie
   */
  def rendLaCopieTeste() {
    Copie copie = Copie.get(params.copie.id)
    def nombreReponses = params.nombreReponses as Integer
    ListeReponsesCopie reponsesCopie = new ListeReponsesCopie()
    nombreReponses.times {
      reponsesCopie.listeReponses << new ReponseCopie()
    }
    bindData(reponsesCopie, params, "reponsesCopie")

    reponsesCopie.listeReponses.each { ReponseCopie reponseCopie ->
      def reponse = Reponse.get(reponseCopie.reponse.id)
      reponseCopie.reponse = reponse
      reponseCopie.specificationObject = reponseService.getSpecificationReponseInitialisee(reponse)
    }
    bindData(reponsesCopie, params, "reponsesCopie")
    Personne eleve = authenticatedPersonne
    copieService.updateCopieForListeReponsesCopie(copie,
                                                  reponsesCopie.listeReponses,
                                                  eleve)
    request.messageCode = "copie.enregistre.succes"

    render(view: '/sujet/teste', model: [
            liens: breadcrumpsService.liens,
            lienRetour: breadcrumpsService.lienRetour(),
            copie: copie,
            afficheCorrection: true
    ])
  }

  /**
   *
   * Action "editeProprietes"
   */
  def editeProprietes() {
    breadcrumpsService.manageBreadcrumps(params, message(code: "sujet.editeproprietes.titre"))
    Sujet sujet = Sujet.get(params.id)
    Personne proprietaire = authenticatedPersonne
    render(view: "editeProprietes", model: [
            liens: breadcrumpsService.liens,
            lienRetour: breadcrumpsService.lienRetour(),
            sujet: sujet,
            typesSujet: sujetService.getAllSujetTypes(),
            matieres: profilScolariteService.findMatieresForPersonne(proprietaire),
            niveaux: profilScolariteService.findNiveauxForPersonne(proprietaire)
    ])
  }

  /**
   *
   * Action supprime element
   */
  def supprimeFromSujet() {
    SujetSequenceQuestions sujetQuestion = SujetSequenceQuestions.get(params.id)
    Personne proprietaire = authenticatedPersonne
    Sujet sujet = sujetService.supprimeQuestionFromSujet(sujetQuestion, proprietaire)
    render(view: '/sujet/edite', model: [
            sujet: sujet,
            titreSujet: sujet.titre,
            sujetEnEdition: true,
            liens: breadcrumpsService.liens,
            artefactHelper: artefactAutorisationService,
            utilisateur: proprietaire
    ])
  }

/**
 *
 * Action remonte element
 */
  def remonteElement() {
    SujetSequenceQuestions sujetQuestion = SujetSequenceQuestions.get(params.id)
    Personne proprietaire = authenticatedPersonne
    Sujet sujet = sujetService.inverseQuestionAvecLaPrecedente(sujetQuestion, proprietaire)
    render(view: '/sujet/edite', model: [
            sujet: sujet,
            titreSujet: sujet.titre,
            sujetEnEdition: true,
            liens: breadcrumpsService.liens,
            artefactHelper: artefactAutorisationService,
            utilisateur: proprietaire
    ])
  }

  /**
   *
   * Action remonte element
   */
  def descendElement() {
    SujetSequenceQuestions sujetQuestion = SujetSequenceQuestions.get(params.id)
    Personne proprietaire = authenticatedPersonne
    Sujet sujet = sujetService.inverseQuestionAvecLaSuivante(sujetQuestion, proprietaire)
    render(view: '/sujet/edite', model: [
            sujet: sujet,
            titreSujet: sujet.titre,
            sujetEnEdition: true,
            liens: breadcrumpsService.liens,
            artefactHelper: artefactAutorisationService,
            utilisateur: proprietaire
    ])
  }

  /**
   *
   * Action "enregistrer"
   */
  def enregistre(NouveauSujetCommand sujetCmd) {
    Sujet sujet
    boolean sujetEnEdition = false
    Personne personne = authenticatedPersonne
    if (sujetCmd.sujetId) {
      sujet = Sujet.get(sujetCmd.sujetId)
      sujetEnEdition = true
      sujetService.updateTitreSujet(sujet, sujetCmd.sujetTitre, personne)
    } else {
      sujet = sujetService.createSujet(personne, sujetCmd.sujetTitre)
    }
    if (!sujet.hasErrors()) {
      request.messageCode = "sujet.enregistre.succes"
      if (!sujetEnEdition) {
        def params = [:]
        params."${Breadcrumps.PARAM_BREADCRUMPS_INIT}" = true
        params.id = sujet.id
        params.action = "edite"
        params.controller = "sujet"
        breadcrumpsService.manageBreadcrumps(params,
                                             message(code: "sujet.edite.titre"))
        sujetEnEdition = true
      }
    }
    render(view: "edite", model: [
            liens: breadcrumpsService.liens,
            titreSujet: sujet.titre,
            sujet: sujet,
            sujetEnEdition: sujetEnEdition,
            artefactHelper: artefactAutorisationService,
            utilisateur: personne
    ])
  }

  /**
   *
   * Action "enregistrerPropriete
   */
  def enregistrePropriete() {
    Sujet sujet = Sujet.get(params.id)
    Personne proprietaire = authenticatedPersonne
    Sujet sujetModifie = sujetService.updateProprietes(sujet, params, proprietaire)
    if (!sujet.hasErrors()) {
      request.messageCode = "sujet.enregistre.succes"
    }
    render(view: "editeProprietes", model: [
            liens: breadcrumpsService.liens,
            lienRetour: breadcrumpsService.lienRetour(),
            sujet: sujetModifie,
            typesSujet: sujetService.getAllSujetTypes(),
            matieres: profilScolariteService.findMatieresForPersonne(proprietaire),
            niveaux: profilScolariteService.findNiveauxForPersonne(proprietaire)
    ])
  }

  /**
   *
   * Action ajoute element
   */
  def ajouteElement() {
    def props = null
    if (params.rang) {
      def rang = params.rang as Integer
      props = [:]
      if (PARAM_DIRECTION_AVANT == params.direction) {
        props."${PROP_RANG_INSERTION}" = rang
      } else {
        props."${PROP_RANG_INSERTION}" = rang + 1
      }
    }
    breadcrumpsService.manageBreadcrumps(params, message(code: "sujet.ajouteelement.titre"), props)
    Sujet sujet = Sujet.get(params.id)

    [
            sujet: sujet,
            liens: breadcrumpsService.liens,
            typesQuestionSupportes: questionService.typesQuestionsInteractionSupportes
    ]
  }

  /**
   * Action ajoute séance
   */
  def ajouteSeance() {
    breadcrumpsService.manageBreadcrumps(params, message(code: "sujet.ajouteseance.titre"))
    Personne personne = authenticatedPersonne
    Sujet sujet = Sujet.get(params.id)
    def modaliteActivite = new ModaliteActivite(enseignant: personne,
                                                sujet: sujet)
    def proprietesScolarite = profilScolariteService.findProprietesScolariteWithStructureForPersonne(
            personne)
    render(view: '/seance/edite', model: [
            liens: breadcrumpsService.liens,
            lienRetour: breadcrumpsService.lienRetour(),
            modaliteActivite: modaliteActivite,
            proprietesScolarite: proprietesScolarite
    ])
  }

  /**
   *
   * Action pour mettre à jour le nombre de  points associé à une question
   */
  def updatePoints(UpdatePointsCommand pointsCommand) {
    try {
      // deduit l'id de l'objet SujetSequenceQuestions à modifier
      def id = pointsCommand.element_id - "SujetSequenceQuestions-" as Long
      def sujetQuestion = SujetSequenceQuestions.get(id)
      // récupère la nouvelle valeur
      def points = pointsCommand.update_value
      // met à jour
      sujetService.updatePointsForQuestion(points,
                                           sujetQuestion,
                                           authenticatedPersonne)
      render NumberUtils.formatFloat(points)
    } catch (Exception e) {
      log.info(e.message)
      render params.original_html
    }
  }

}

class UpdatePointsCommand {
  String element_id
  Float update_value
}

class NouveauSujetCommand {
  String sujetTitre
  Long sujetId
}

class RechercheSujetCommand {
  String patternTitre
  String patternAuteur
  String patternPresentation

  Long matiereId
  Long typeId
  Long niveauId

  Map toParams() {
    [
            patternTitre: patternTitre,
            patternAuteur: patternAuteur,
            patternPresentation: patternPresentation,
            matiereId: matiereId,
            typeId: typeId,
            niveauId: niveauId
    ]
  }

}