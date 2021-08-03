package com.github.solayw.webutil

import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

@kotlin.annotation.Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Code

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.github.solayw.webutil.Code")
open class ConverterProcessor(): AbstractProcessor() {


    private lateinit var filer: Filer
    override fun init(processingEnv: ProcessingEnvironment?) {
        filer = processingEnv!!.filer
    }
    override fun process(elements: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(Code::class.java)
        for (x in elements) {
            val type = x as TypeElement
            genSource(type)
        }
        return true
    }


    private fun genSource(ele: TypeElement){

        val className = ele.qualifiedName
        val pkg = className.subSequence(0, className.lastIndexOf('.'))
        val convertName = ele.simpleName.toString() + "JpaConverter"
        val code = """
                    package $pkg;
                    @javax.persistence.Converter(
                        autoApply = true
                    )
                    public class $convertName implements javax.persistence.AttributeConverter<$className, Integer> {
                        
                        public $className convertToEntityAttribute(Integer dbData) {
                            if(dbData == null) {
                                return null;
                            }
                            for(OrderState x : $className.values()) {
                                if(x.code == dbData) {
                                    return x;
                                }
                            }
                            throw new RuntimeException("unknown code " + dbData  + " for " + $className.class.getName());
                        }
                    
                        public Integer convertToDatabaseColumn($className attribute) {
                            return attribute.code;
                        }
                    }
"""
        val sourceFile = filer.createSourceFile("${pkg}.${convertName}")
        val writer = sourceFile.openWriter()
        writer.write(code)
        writer.close()
    }



}