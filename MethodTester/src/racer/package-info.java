package racer;

import java.lang.annotation.Documented;

@Documented
@interface Preamble {
	String author() default "Michael Newman";
	String date();
	String description();
	String assumptions() default "N/A";
	String limitations() default "N/A";
	String I_O() default "N/A";
	String[] references() default {"N/A"};
	int currentVersion() default 1;
	String lastModified() default "N/A";
	String lastModifiedBy() default "N/A";
 }

@Documented
@interface NestedClassPreamble{
	String description();
}

@Documented
@interface MethodPreamble {
	String description();
	String returns() default "N/A";
	String[] parameters() default "N'A";
	String[] throwsExceptions() default "N/A";
}