#!make

M := "mvn"

.PHONY: build
build: fmt
	${M} install

.PHONY: deploy
deploy: fmt
	${M} deploy

.PHONY: compile
compile: fmt
	${M} compile test-compile

.PHONY: test
test: fmt
	${M} test

.PHONY: package
package: fmt
	${M} package

.PHONY: verify
verify: fmt
	${M} verify

.PHONY: fmtCheck
fmtCheck:
	${M} spotless:check

.PHONY: fmt
fmt:
	${M} spotless:apply

.PHONY: clean
clean:
	${M} clean
