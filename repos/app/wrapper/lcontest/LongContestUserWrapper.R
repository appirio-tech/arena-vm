library(compiler)
library(methods)
library(R.oo)
library(R.utils)
library(fork)
dyn.load("<C_HELPER>")

INT_TYPE        <- as.integer(1)
LONG_TYPE       <- as.integer(2)
DOUBLE_TYPE     <- as.integer(3)
STRING_TYPE     <- as.integer(4)
VEC_INT_TYPE    <- as.integer(5)
VEC_LONG_TYPE   <- as.integer(6)
VEC_DOUBLE_TYPE <- as.integer(7)
VEC_STRING_TYPE <- as.integer(8)

EXCEPTION       <- as.integer(250)
TIMEOUT         <- as.integer(251)
ABORT           <- as.integer(252)
METHOD_START    <- as.integer(253)
TIME            <- as.integer(254)
TERMINATE       <- as.integer(255)

fin <- as.integer(11)
fout <- as.integer(12)

flush <- function() {
    .C("flush",fout)
}

flush2 <- function(fd) {
    .C("flush",fd)
}

writeSingleInt <- function(i) {
    .C("writeSingleInt",fout,i)
}

writeSingleInt2 <- function(fd,i) {
    .C("writeSingleInt",fd,i)
}

writeInt2 <- function(i) {
    .C("writeInt",fout,i)
}

writeInt3 <- function(fd,i) {
    .C("writeInt",fd,i)
}

writeInt <- function(i) {
    .C("writeSingleInt",fout,INT_TYPE)
    writeInt2(i)
}

writeLong <- function(ll) {
    .C("writeSingleInt",fout,LONG_TYPE)
    .C("writeLongLong",fout,ll)
}

writeDouble <- function(d) {
    .C("writeSingleInt",fout,DOUBLE_TYPE)
    .C("writeDouble",fout,d)
}

writeString2 <- function(str) {
    strLen <- nchar(str)
    writeInt2(strLen)
    for(n in 1:strLen) {
        everyChar <- charToInt(substr(str,n,n))
        writeSingleInt(as.integer(everyChar))
    }
}

writeString <- function(str) {
    .C("writeSingleInt",fout,STRING_TYPE)
    writeString2(str)
}

writeIntArray <- function(i_arr) {
    .C("writeSingleInt",fout,VEC_INT_TYPE)
    len <- length(i_arr)
    for(n in 1:len) {
        writeInt(i_arr[n])
    }
}

writeLongArray <- function(l_arr) {
    .C("writeSingleInt",fout,VEC_LONG_TYPE)
    len <- length(l_arr)
    for(n in 1:len) {
        writeLong(l_arr[n])
    }
}

writeDoubleArray <- function(d_arr) {
    .C("writeSingleInt",fout,VEC_DOUBLE_TYPE)
    len <- length(d_arr)
    for(n in 1:len) {
        writeLong(d_arr[n])
    }
}

writeStringArray <- function(str_arr) {
    .C("writeSingleInt",fout,VEC_STRING_TYPE)
    len <- length(str_arr)
    writeInt2(len)
    for(n in 1:len) {
        writeString2(str_arr[n])
    }
}

writeMethod <- function(i) {
    writeSingleInt(METHOD_START)
    writeInt2(i)
}

writeTime <- function(t) {
    writeSingleInt(TIME)
    writeInt2(t)
}

writeException <- function(e) {
    writeSingleInt(EXCEPTION)
    writeInt2(e)
}

writeArg <- function(val,type) {
    if(type == INT_TYPE) {
        writeInt(val)
    } else if(type == LONG_TYPE) {
        writeLong(val)
    } else if(type == DOUBLE_TYPE) {
        writeDouble(val)
    } else if(type == VEC_INT_TYPE) {
        writeIntArray(val)
    } else if(type == VEC_LONG_TYPE) {
        writeLongArray(val)
    } else if(type == VEC_DOUBLE_TYPE) {
        writeDoubleArray(val)
    } else if(type == VEC_STRING_TYPE) {
        writeStringArray(val)
    }
}

getInt <- function() {
    s1 <- .C("getSingleInt",fin,result=as.integer(10))
    return (s1$result)
}

getInt2 <- function() {
    s2 <- .C("getInt2",fin,result=as.integer(12))
    return (s2$result)
}

getLong2 <- function() {
    s2 <- .C("getLongLong2",fin,result=as.numeric(12))
    return (s2$result)
}

getLong <- function() {
    type <- getInt()
    return (getLong2())
}

getDouble2 <- function() {
    s2 <- .C("getDouble2",fin,result=as.double(12))
    return (s2$result)
}

getDouble <- function() {
    type <- getInt()
    return (getDouble2())
}

getString2 <- function() {
    b <- as.character("")
    len <- getInt2()
    for(n in 1:len) {
            c <- getInt()
            b <- paste(b,intToChar(c),sep="")
    }
    return (b)
}
getString <- function() {
    type <- getInt()
    return (getString2())
}

getStringArray <- function() {
    ret <- list()
    type <- getInt()
    strLen <- getInt2()
    for(n in 1:strLen) {
        line <- getString2()
        ret[n] <- line
    }
    return (ret)
}

getIntArray <- function() {
    type <- getInt()
    i_len <- getInt2()
    ret <- list()
    for(n in 1:i_len) {
        ret[n] <- getInt2()
    }
    return (ret)
}

getDoubleArray <- function() {
    type <- getInt()
    d_len <- getInt2()
    ret <- list()
    for(n in 1:d_len) {
        ret[n] <- getDouble2()
    }
    return (ret)
}

getLongArray <- function() {
    type <- getInt()
    l_len <- getInt2()
    ret <- list()
    for(n in 1:l_len) {
        ret[n] <- getLong2()
    }
    return (ret)
}

gettime <- function() {
    return (System$currentTimeMillis())
}

startMethod <- function(fd, a) {
    writeSingleInt2(fd,METHOD_START)
    writeInt3(fd,a)
}
main <- function() {
    initialized <- FALSE
    mod <- ""
    sol <- ""
    timercnt <- as.integer(0)
    pid <- getpid()
    while ( TRUE ) {
        command <- getInt()
        if(command == TERMINATE || command == -1) {
            exit(0)
        } else if(command == TIMEOUT) {
            d <- getInt2()
        } else if(command == METHOD_START) {
            method <- getInt2()
            if(method == -1) {
                #do nothing
            } 
            <METHODS>
            else if(method == <METHOD_NUMBER>) {
                <ARGS>
                    <ARG_NAME> = <ARG_METHOD_NAME>()
                </ARGS>
				
				
                kill(pid, signal = 27)
                tm <- gettime()

                timercnt <- timercnt + 1
                if(!initialized) {
                    solPath <- paste(scriptPath,"/<CLASS_NAME>.rlc",sep="")
                	loadcmp(solPath)
                	sol <- new ("<CLASS_NAME>", <PARAMS>)
                    initialized = TRUE
                }
                
                sol <- initialize(sol,<PARAMS>)

                ret <- <METHOD_NAME>(sol)

				solPath <- paste(scriptPath,"/<EXPOSED_WRAPPER_CLASS>.rlc",sep="")
                loadcmp(solPath)

				tm <- gettime() - tm - getExposedTime()
				
                timercnt <- timercnt + 1

                if(tm > 0 && timercnt >= 50) {
                    tm <- tm - 1
                    timercnt <- timercnt - 50
                }

                kill(pid, signal = 27)

                writeTime(as.integer(tm))
                writeArg(as.integer(1),INT_TYPE)
                <RET_METHOD_NAME>(ret)
                flush()
            }
            </METHODS>
        }
    }
}
main()