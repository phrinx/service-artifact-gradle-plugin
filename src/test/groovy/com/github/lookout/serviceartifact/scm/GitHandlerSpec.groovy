package com.github.lookout.serviceartifact.scm

import org.eclipse.jgit.errors.RepositoryNotFoundException
import spock.lang.*

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Repository

class GitHandlerSpec extends Specification {
    def "isAvailable() should be false by default"() {
        given:
        def handler = Spy(GitHandler, constructorArgs: [[:]])
        1 * handler.getProperty('git') >> null

        expect:
        !handler.isAvailable()
    }


    def "isAvailable() should be true if .git is present"() {
        given:
        def handler = Spy(GitHandler, constructorArgs: [[:]])
        def gitMock = Mock(Grgit)
        1 * handler.getProperty('git') >> gitMock

        expect:
        handler.isAvailable()
    }


    def "annotatedVersion() when .git is NOT present should no-op"() {
        given:
        def handler = new GitHandler([:])
        GroovySpy(Grgit, global: true)
        1 * Grgit.open(_) >> { throw new RepositoryNotFoundException('Spock!') }

        when:
        String version = handler.annotatedVersion('1.0')

        then:
        version == '1.0'
    }

    def "annotatedVersion() when .git is present should include SHA+1"() {
        given:
        def handler = Spy(GitHandler, constructorArgs: [[:]])
        Repository repoMock = GroovyMock()
        1 * repoMock.head() >> repoMock
        1 * repoMock.getId() >> '0xdeadbeef'
        _ * handler.getProperty('git') >> repoMock

        when:
        String version = handler.annotatedVersion('1.0')

        then:
        version == '1.0+0xdeadbeef'
    }
}
