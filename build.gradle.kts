modtype = JVM_ONLY

plugins {
  kotlin("plugin.serialization")
  kotlin("multiplatform")
}


//implementations(
//  ":k:klib"
//)


configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
  sourceSets {
	val commonMain by getting {
	  this.dependencies {
		implementation(libs.kotlinx.coroutines)
		implementations(
		  ":k:klib".auto(),
		  handler = this
		)
		//		if (rootDir.name.upper() == "FLOW") {
		//		  implementation(project(":k:klib"))
		//		} else {
		//		  todo("this is bad")
		//		implementation("matt.flow:klib:+")
		//		}
	  }
	}
  }
}