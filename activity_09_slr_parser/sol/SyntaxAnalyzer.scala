/*
 * CS3210 - Principles of Programming Languages - Fall 2021
 * Instructor: Thyago Mota
 * Description: Activity 09 - Syntax Analyzer
 */

import SyntaxAnalyzer.{GRAMMAR_FILENAME, SLR_TABLE_FILENAME}
import Token.Value

import scala.collection.mutable.ArrayBuffer

/*
expression  = term expression'
expression' = ( ´+´  | ´-´ ) term expression' | epsilon
term        = factor term'
term'       = ( ´*´ | ´/´ ) factor term' | epsilon
factor      = identifier | literal | ´(´ expression ´)´
identifier  = letter { ( letter | digit ) }
letter      = ´a´ | ´b´ | ´c´ | ´d´ | ´e´ | ´f´ | ´g´ | ´h´ | ´i´ | ´j´ | ´k´ | ´l´ | ´m´
| ´n´ | ´o´ | ´p´ | ´q´ | ´r´ | ´s´ | ´t´ | ´u´ | ´v´ | ´w´ | ´x´ | ´y´ | ´z´
literal     = digit { digit }
digit       = ´0´ | ´1´ | ´2´ | ´3´ | ´4´ | ´5´ | ´6´ | ´7´ | ´8´ | ´9´
 */

class SyntaxAnalyzer(private var source: String) {

  private val it = new LexicalAnalyzer(source).iterator
  private var current: Lexeme = null
  private val grammar = new Grammar(GRAMMAR_FILENAME)
  private val slrTable = new SLRTable(SLR_TABLE_FILENAME)

  // returns the current lexeme
  private def getLexeme(): Lexeme = {
    if (current == null) {
      current = it.next
    }
    //    println(current)
    current
  }

  // advances the input one lexeme
  private def nextLexeme() = {
    current = it.next
  }

  def parse(): Tree = {

    // create a stack of trees
    val trees: ArrayBuffer[Tree] = new ArrayBuffer[Tree]

    // initialize the parser's stack of (state, symbol) pairs
    val stack: ArrayBuffer[String] = new ArrayBuffer[String]
    stack.append("0")

    // main parser loop
    while (true) {

      if (SyntaxAnalyzer.DEBUG)
        println("stack: " + stack.mkString(","))

      // get current lexeme
      val lexeme = getLexeme()

      // get current state
      var state = stack.last.strip().toInt
      if (SyntaxAnalyzer.DEBUG)
        println("state: " + state)

      // get current token from lexeme
      val token = lexeme.getToken()

      // get action
      val action = slrTable.getAction(state, token.id)
      if (SyntaxAnalyzer.DEBUG)
        println("action: " + action)

      // TODOd: if action is undefined, throw an exception
      if (action.length == 0)
        throw new Exception("Syntax Analyzer Error!")

      // implement the "shift" operation if the action's prefix is "s"
      if (action(0) == 's') {

        // TODOd: update the parser's stack
        stack.append(token + "")
        stack.append(action.substring(1))

        // TODOd: create a new tree with the lexeme
        val tree = new Tree(lexeme.getLabel())

        // TODOd: push the new tree onto the stack of trees
        trees.append(tree)

        // acknowledge reading the input
        nextLexeme()
      }
      // implement the "reduce" operation if the action's prefix is "r"
      else if (action(0) == 'r') {

        // TODOd: get the production to use
        val index = action.substring(1).toInt
        val lhs = grammar.getLHS(index)
        val rhs = grammar.getRHS(index)

        // TODOd: update the parser's stack
        stack.dropRightInPlace(rhs.length * 2)
        state = stack.last.strip().toInt
        val new_state = slrTable.getGoto(state, lhs)
        stack.append(lhs)
        stack.append(new_state)

        // TODOd: create a new tree with the "lhs" variable as its label
        val newTree = new Tree(lhs)

        // TODOd: add "rhs.length" trees from the right-side of "trees" as children of "newTree"
        for (tree <- trees.drop(trees.length - rhs.length))
          newTree.add(tree)

        // TODOd: drop "rhs.length" trees from the right-side of "trees"
        trees.dropRightInPlace(rhs.length)

        // TODOd: append "newTree" to the list of "trees"
        trees.append(newTree)
      }
      // implement the "accept" operation
      else if (action.equals("acc")) {

        // create a new tree with the "lhs" of the first production ("start symbol")
        val newTree = new Tree(grammar.getLHS(0))

        // add all trees as children of "newTree"
        for (tree <- trees)
          newTree.add(tree)

        // return "newTree"
        return newTree
      }
      else
        throw new Exception("Syntax Analyzer Error!")
    }
    throw new Exception("Syntax Analyzer Error!")
  }
}

object SyntaxAnalyzer {

  val GRAMMAR_FILENAME   = "grammar.txt"
  val SLR_TABLE_FILENAME = "slr_table.csv"
  val DEBUG = false

  def main(args: Array[String]): Unit = {
    // check if source file was passed through the command-line
    if (args.length != 1) {
      print("Missing source file!")
      System.exit(1)
    }

    val syntaxAnalyzer = new SyntaxAnalyzer(args(0))
    val parseTree = syntaxAnalyzer.parse()
    print(parseTree)
  }
}