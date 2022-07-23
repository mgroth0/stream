
plugins {
  kotlin("plugin.serialization")
  kotlin("multiplatform")
}



configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
  sourceSets {
	val commonMain by getting {
	  dependencies {
		implementation(libs.kotlinx.coroutines)
		implementations(
		  ":k:klib".auto(),
		  handler = this
		)
	  }
	}
  }
}