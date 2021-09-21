/*
 * CS3210 - Principles of Programming Languages - Fall 2021
 * Instructor: Thyago Mota
 * Description: Activity 08 - Token
 */

/*
syntax            = syntax-rule { ´\n´ syntax-rule }
syntax-rule       = meta-identifier ´=´ definitions-list
meta-identifier   = letter { ( letter | digit | ´-´ | ´_´ ) }
definitions-list  = single-definition { ´|´ single-definition }
single-definition = term { term }
term              = optional-sequence | repeated-sequence | grouped-sequence | meta-identifier | terminal-string
optional-sequence = ´[´ definitions-list ´]´
repeated-sequence = ´{´ definitions-list ´}´
grouped-sequence  = ´(´ definitions-list ´)´
terminal-string   = ´´´ character { character } ´´´
integer           = digit {digit}
 */

object Token extends Enumeration {
  val EOF             = Value
  val NEW_LINE        = Value // \n
}
