/*************
 * Copyright (c) 2019-2021, TU Dresden.

 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:

 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.

 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ***************/

package org.lflang.generator.cpp

import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.resource.Resource
import org.lflang.lf.Preamble
import org.lflang.scoping.LFGlobalScopeProvider


class CppPreambleGenerator(
    private val resource: Resource,
    private val fileConfig: CppFileConfig,
    private val scopeProvider: LFGlobalScopeProvider
) {
    /** A list of all preambles defined in the resource (file) */
    private val preambles: EList<Preamble> = resource.model.preambles

    fun generateHeader(): String {
        val importedResources = scopeProvider.getImportedResources(resource)
        val includes = importedResources.map { fileConfig.getPreambleHeaderPath(it) }

        val publicPreambles = preambles.filter { it.isPublic }

        return with(prependOperator) {
            """
            ${" |"..fileComment(resource)}
                |
                |#pragma once
                |
                |#include <vector>
                |#include <array>
                |
                |#include "reactor-cpp/reactor-cpp.hh"
            ${" |"..includes.joinToString(separator = "\n", prefix = "// include the preambles from imported files \n")}
                |
            ${" |"..publicPreambles.joinToString(separator = "\n")}
            """.trimMargin()
        }
    }

    fun generateSource(): String {
        val privatePreambles = preambles.filter { it.isPrivate }

        return with(prependOperator) {
            """
            ${" |"..fileComment(resource)}
                |
                |#include "reactor-cpp/reactor-cpp.hh"
                |
                |#include "${fileConfig.getPreambleHeaderPath(resource).toUnixString()}"
                |
                |using namespace std::chrono_literals;
                |using namespace reactor::operators;
                |
            ${" |"..privatePreambles.joinToString(separator = "\n")}
            """.trimMargin()
        }
    }
}