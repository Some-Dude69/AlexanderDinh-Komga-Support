package FilesUtil.file_fixer
import FFU.Character_OBJ
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Paths
import javax.swing.JOptionPane

object XML_Create{

    fun WriteXML(charObj: Character_OBJ) {
        try {
            BufferedWriter(
                FileWriter(Paths.get(charObj.Character_Path, "ComicInfo.xml").toFile()), 1200
            ).use { writer ->
                writer.write(
                    """<?xml version="1.0" encoding="utf-8"?>
<ComicInfo>
  <Title>${charObj.Character}</Title>
  <Series>${charObj.Month} ${charObj.Year}</Series>
  <Summary>The artist "Alexander Dinh" made this in:  
	${charObj.Month} ${charObj.Year}</Summary>
  <Notes>lewd</Notes>
  <Year>${charObj.Year}</Year>
  <Month>${charObj.Month_Number}</Month>
  <Inker>Alexander Dinh</Inker>
  <Colorist>Alexander Dinh</Colorist>
  <CoverArtist>Alexander Dinh</CoverArtist>
  <Publisher>Patreon, Gum Road</Publisher>
  <Genre>lewd</Genre>
  <Web>https://alexanderdinh.gumroad.com/, https://www.patreon.com/alexanderdinh</Web>
  <BlackAndWhite>No</BlackAndWhite>
  <Manga>No</Manga>
  <Characters>${charObj.Character}</Characters>
  <SeriesGroup>Alexander Dinh</SeriesGroup>
  <AgeRating>X18+</AgeRating>
  <CommunityRating>5.00</CommunityRating>
  <MainCharacterOrTeam>${charObj.Character}</MainCharacterOrTeam>
</ComicInfo>""".trimIndent()
                )
            }
        } catch (e: IOException) {
            JOptionPane.showMessageDialog(
                null,
                "Something went wrong when writing the XML",
                "ERR",
                JOptionPane.ERROR_MESSAGE
            )
            throw RuntimeException("Something went wrong when writing the XML")
        }
    }
}