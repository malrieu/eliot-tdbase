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



package org.lilie.services.eliot.tice

import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.FileImageInputStream
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.lilie.services.eliot.tice.annuaire.Personne
import org.lilie.services.eliot.tice.utils.ServiceEliotEnum
import org.lilie.services.eliot.tice.utils.ServicesEliotService
import org.springframework.web.multipart.MultipartFile

/**
 * Classe fournissant le service de gestion de breadcrumps
 * @author franck silvestre
 */
class AttachementService {

  static transactional = false
  ServicesEliotService servicesEliotService

  /**
   * Créer un objet de type Attachement à partir d'une requête HTTP
   * encapsulant la pièce jointe
   * @param fichier l'objet fichier provenant de la requête
   * @param serviceEliotEnum le service eliot concerné
   * @param proprietaire le proprietaire du fichier
   * @param config le config object
   * @return l'objet Attachment
   */
  Attachement createAttachementForMultipartFile(
          MultipartFile fichier,
          ServiceEliotEnum serviceEliotEnum,
          Personne proprietaire,
          def config = ConfigurationHolder.config) {
    if (!fichier || fichier.isEmpty()) {
      throw new IllegalArgumentException("question.document.fichier.vide")
    }
    if (!fichier.name) {
      throw new IllegalArgumentException("question.document.fichier.nom.null")
    }
    def maxSizeEnMega = config.eliot.fichiers.maxsize.mega
    if (fichier.size > 1024 * 1024 * maxSizeEnMega) {
      throw new IllegalArgumentException("question.document.fichier.tropgros")
    }
    // par defaut un nouvel attachement est marque a supprimer
    // c'est à la création d'un lien vers un item qu'il faut le
    // considérer comme attaché et donc comme non à supprimer
    Attachement attachement = new Attachement(
            taille: fichier.size,
            typeMime: fichier.contentType,
            nom: fichier.originalFilename,
            nomFichierOriginal: fichier.originalFilename,
            aSupprimer: true
    )
    String racine = servicesEliotService.getCheminRacineEspaceFichier(config)
    String espaceFic = servicesEliotService.getCheminRelatifEspaceFichierForPersonneAndServiceEliot(
            proprietaire, serviceEliotEnum
    )
    String fs = File.separator
    Date now = new Date()
    String espaceDest = espaceFic << now.format("yyyy") << fs << now.format("MM")
    String racineDest = racine + espaceDest
    new File(racineDest).mkdirs()
    String cheminRel = espaceDest << fs << UUID.randomUUID()
    String cheminDest = racine + cheminRel
    File fichierDest = new File(cheminDest)
    fichier.transferTo(fichierDest)
    attachement.chemin = cheminRel
    attachement.dimension = determinerDimension(fichierDest)
    attachement.save()
    return attachement
  }

  /**
   * Retourne l'objet File correspondant à un attachement
   * @param attachement l'attachement
   *
   * @param config le config object
   * @return l'objet de type File
   */
  File getFileForAttachement(Attachement attachement,
                             def config = ConfigurationHolder.config) {
    String chemin = servicesEliotService.getCheminRacineEspaceFichier(config) <<
                    attachement.chemin
    new File(chemin)
  }

  /**
   * Determine les dimensions d'un image.
   * @param imageFile objet du fichier de l'image à analyser
   * @param fileName non d'origine du fichier.
   * @return les dimensions du fichier.
   */
  private Dimension determinerDimension(File imageFile) {

    ImageReader reader

    try {
      def fileInputStream = new FileImageInputStream(imageFile)
      def imageReaders = ImageIO.getImageReaders(fileInputStream)

      if (imageReaders.hasNext()) {
        reader = imageReaders.next()
        reader.input = fileInputStream
        return new Dimension(
                largeur: reader.getWidth(reader.minIndex),
                hauteur: reader.getHeight(reader.minIndex)
        )
      }

    } finally {
      reader?.dispose()
    }
  }
}
