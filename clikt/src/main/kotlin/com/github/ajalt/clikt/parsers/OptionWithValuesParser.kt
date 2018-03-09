package com.github.ajalt.clikt.parsers

import com.github.ajalt.clikt.core.BadOptionUsage
import com.github.ajalt.clikt.parameters.options.Option
import com.github.ajalt.clikt.parsers.OptionParser.ParseResult

object OptionWithValuesParser : OptionParser {
    override fun parseLongOpt(option: Option, name: String, argv: Array<String>,
                              index: Int, explicitValue: String?): ParseResult {
        require(option.nargs > 0) {
            "This parser can only be used with a fixed number of arguments. Try the flag parser instead."
        }
        val hasIncludedValue = explicitValue != null
        val consumedCount = if (hasIncludedValue) option.nargs else option.nargs + 1
        val endIndex = index + consumedCount - 1

        if (endIndex > argv.lastIndex) {
            throw BadOptionUsage(if (option.nargs == 1) {
                "$name option requires an argument"
            } else {
                "$name option requires ${option.nargs} arguments"
            })
        }

        val invocation = if (option.nargs > 1) {
            var args = argv.slice((index + 1)..endIndex)
            if (explicitValue != null) args = listOf(explicitValue) + args
            OptionParser.Invocation(name, args)
        } else {
            OptionParser.Invocation(name, listOf(explicitValue
                    ?: argv[index + 1]))
        }
        return ParseResult(consumedCount, invocation)
    }

    override fun parseShortOpt(option: Option, name: String, argv: Array<String>,
                               index: Int, optionIndex: Int): ParseResult {
        val opt = argv[index]
        val hasIncludedValue = optionIndex != opt.lastIndex
        val explicitValue = if (hasIncludedValue) opt.substring(optionIndex + 1) else null
        return parseLongOpt(option, name, argv, index, explicitValue)
    }
}
