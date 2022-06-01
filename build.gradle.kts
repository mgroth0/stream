import matt.klib.str.upper

/*NPM INSTALL TASK IS DISABLED IN ROOT BUILDSCRIPT BECAUSE IT PRODUCES OBNOXIOUS WARNING. WILL NEED TO ENABLE THAT TO INSTALL DEPENDENCIES PROBABLY*/

//dependencies {
//  implementations(projects.k.klib)
//}


plugins {
  kotlin("plugin.serialization")
  kotlin("multiplatform")
  /*experimental.coroutines = org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE*/
}

configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
  sourceSets {
	val commonMain by getting {
	  this.dependencies {
		if (rootDir.name.upper() == "FLOW") {
		  implementation(project(":k:klib"))
		} else {
		  implementation("matt.k:klib:+")
		}
	  }
	}
  }
}