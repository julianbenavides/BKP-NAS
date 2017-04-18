/*Script to copy the CVS backup into NAS*/
class CopyCVSBackup 
{
	static void main(String[] args)
	{
		/*Build filename and path*/

		//bckpath can be anything
		def bckpath = "\\BackupPath"
		//NAS path; can be anything
		def bckdst = "\\SMB\Destination\Path"

		def today = new Date()
		def yesterday = today - 1
		//debug point
		//println yesterday.format("yyyyMMdd")+".zip"
		
		//By default we'll move the backup from the day before IF it exists.
		def finame = yesterday.format("yyyyMMdd")
		
		//Creating file object to check file existence
		def srcfile = new File(bckpath+finame+".zip")

		//Creating folder object to allow deletion after the backup is moved.
		def srcfldr = new File(bckpath+finame)

		

		//Creating log objects
		def now = new Date()
		def timestamp = now.toTimestamp()
		def log = new File(bckpath+finame+"log.txt")

		log << "${timestamp}: Starting script log\n"
		log << "${timestamp}: Backup origin path defined: ${bckpath}\n"
		log << "${timestamp}: Backup destination path defined: ${bckdst}\n"
		log << "${timestamp}: Folder and File name definition ${finame}\n"

		
		//Check if file exists; if so copy to NAS
		if(srcfile.exists() && srcfile.isFile() && !srcfile.isDirectory()) 
		{
          
          //using streams to avoid memory issues
		def src = new File(bckpath+finame+".zip").newInputStream()
			//creating dstfile object to verify size and existence using the File class methods
			def dstfile = new File(bckdst+finame+".zip")
			

			def dst = new File(bckdst+finame+".zip").newOutputStream()
			log << "${timestamp}: File ${finame}.zip found at ${bckpath}\n"
			dst << src
			log << "${timestamp}: File ${finame}.zip has been copied into repo. The Raw filesize is ${dstfile.length()} bytes\n"
			src.close()
			dst.close()
			log << "${timestamp}: The file ${finame}.zip has been permanently moved to ${dstfile.absolutePath}\n"
			log << "${timestamp}: The script will try to verify that the file ${finame}.zip was correctly moved, and then it will proceed to purge the CVS folder\n"

			//Verifies if the object is a directory AND if the size of the zip Backup of repo and NAS are the same
			if(srcfldr.isDirectory() && srcfile.length()==dstfile.length())
			{
				log << "${timestamp}: The file ${finame}.zip was moved and was not corrupted during the move. Proceeding to purge CVS\n"
				srcfldr.deleteDir()
				log << "${timestamp}: The folder ${finame}/ has been marked for deletion in repo\n"

			}
			srcfile.delete()
			log << "${timestamp}: The file ${finame}.zip has been marked for deletion in repo\n"
			
		}else
		{
			log << "${timestamp}: The file ${srcfile.absolutePath} does not exist\n"
		}

		log << "${timestamp}: Script finished with no errors.\n"
		
	}
}
/*EOS*/
