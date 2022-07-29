library(fork)

currentTime <- as.integer(0)
startTime <- as.integer(0)

exposed_fd <- as.integer(13)
SIGXFSZ <- 25

reset <- function() {
    currentTime <- as.integer(0)
    startTime <- as.integer(0)
}

start <- function() {
    startTime <- gettime()
}

stop <- function() {
    end <- gettime()
    currentTime <- currentTime + (end - startTime)
}

getExposedTime <- function() {
    return (currentTime)
}

<EXPOSED_METHODS>
<METHOD_NAME> <- function(<PARAMS>):
    start()
    pid <- getpid()

    kill(pid, signal = SIGXFSZ)

    startMethod(exposed_fd, as.integer(<METHOD_NUMBER>))
    
    <WRITE_ARGS>
        <ARG_METHOD>(exposed_fd, <ARG_NAME>)
    </WRITE_ARGS>
        flush2(exposed_fd)
    
    val = <RETURN_METHOD_NAME>(exposed_fd)
    
    kill(pid, signal = SIGXFSZ)
    stop()
    
    return val
</EXPOSED_METHODS>
