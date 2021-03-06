package general
import java.io._

object Config {
	var password = ""

	def testPassword() = {
		try {
			sudoWrite("echo passwordWorking >> /etc/hosts")
			val hostFile = new File("/etc/hosts")
			val hostFileContent = scala.io.Source.fromFile(hostFile).mkString
			if (("passwordWorking".r).findAllIn(hostFileContent).length > 0) {
				general.Config.sudoWrite(s"""sed -i -e "/passwordWorking/d" /etc/hosts""")
				true
			} else {
				false
			}
		} catch {
			case e: Exception => false
		}
	}

	def sudoWrite(command: String) = {
		import sys.process._

		/*
		This is an awful solution to the fact that we need to access the host file as root. Never ever do anything like this.
		Nothing sensible solves this problem so we had to do it the terrible way.
		This is a list of other ways I tried to solve this:

		Just pipe the password into sudo
		Use an output stream to pass the password into the echo
		screen shennanigans

		None of these work. Scala's odd proccess handling thwarts them all
		*/
		val fw = new FileWriter(("./test.sh"), false)
		fw.write(s"""echo $password | sudo -S sh -c '$command'""")
		fw.close()
		"sh ./test.sh" !!;
		(new File("./test.sh")).delete
	}
}