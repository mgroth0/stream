import matt.klib.str.upper

plugins {
  kotlin("plugin.serialization")
  kotlin("multiplatform")
}

configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
  sourceSets {
	val commonMain by getting {
	  this.dependencies {
		implementation(libs.kotlinx.coroutines)
		if (rootDir.name.upper() == "FLOW") {
		  implementation(project(":k:klib"))
		} else {
		  implementation("matt.k:klib:+")
		}
	  }
	}
  }
}