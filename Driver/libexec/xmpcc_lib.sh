function xmpcc_print_help()
{
cat <<EOF
usage: $1 <OPTIONS> <INPUTFILE> ...

Compile Driver Options

   -o <file>         : place the output into <file>.
   -I <dir>          : add the directory dir to the list of directories to be searched for header files.
   -c                : compile and assemble, but do not link.
   -E                : preprocess only; do not compile, assemble or link.
   -v,--verbose      : print processing status.
   --version         : print version.
   -h,--help         : print usage.
   --show-env        : show environment variables.
   --tmp             : output parallel code (__omni_tmp__<file>).
   --dry             : only print processing status (not compile).
   --debug           : save intermediate files in __omni_tmp__.
   --stop-pp         : save intermediate files and stop after preprocess.
   --stop-frontend   : save intermediate files and stop after frontend.
   --stop-translator : save intermediate files and stop after translator.
   --stop-backend    : save intermediate files and stop after backend.
   --stop-compile    : save intermediate files and stop after compile.

Process Options

  --Wp[option] : Add preprocessor option.
  --Wf[option] : Add frontend option.
  --Wx[option] : Add Xcode translator option.
  --Wb[option] : Add backend option.
  --Wn[option] : Add native compiler option.
  --Wl[option] : Add linker option.

XcalableMP Options

  -omp,--openmp       : enable OpenMP.
  -xacc,--xcalableacc : enable XcalableACC.
  --scalasca-all      : output results in scalasca format for all directives.
  --scalasca          : output results in scalasca format for selected directives.
  --tlog-all          : output results in tlog format for all directives.
  --tlog              : output results in tlog format for selected directives.
EOF
}

function xmpcc_show_env()
{
    CONF_FILE="${OMNI_HOME}"/etc/xmpcc.conf
    if [ -f "${CONF_FILE}" ]; then
	for val in `sed '/^[[:space:]]*$/d' "${CONF_FILE}" | grep -v '^#' | awk -F= '{print $1}'`
	do
	    echo -n ${val}=\"
            eval echo -n \"\$$val\"
	    echo \"
	done
    fi
}

function xmpcc_set_parameters()
{
    for arg in "${@}"; do
	case $arg in
	    -o)
                OUTPUT_FLAG=true;;
            -c)
		ENABLE_LINKER=false;;
	    -E)
		ONLY_PP=true;;
            -v|--verbose)
		VERBOSE=true;;
	    --version)
		omni_print_version
		exit 0;;
            -h|--help)
		local scriptname=`basename $0`
		xmpcc_print_help "${scriptname}"
		exit 0;;
	    --show-env)
		xmpcc_show_env
		exit 0;;
            --tmp)
		OUTPUT_TEMPORAL=true;;
            --dry)
		DRY_RUN=true;;
	    --debug)
		ENABLE_DEBUG=true;;
            --stop-pp)
		STOP_PP=true
		VERBOSE=true;;
            --stop-frontend)
		STOP_FRONTEND=true
		VERBOSE=true;;
	    --stop-translator)
		STOP_TRANSLATOR=true
		VERBOSE=true;;
	    --stop-backend)
		STOP_BACKEND=true
		VERBOSE=true;;
	    --stop-compile)
		STOP_COMPILE=true
		VERBOSE=true;;
	    --Wp*)
		pp_add_opt=("${pp_add_opt[@]}" "${arg#--Wp}");;
            --Wf*)
		frontend_add_opt=("${frontend_add_opt[@]}" "${arg#--Wf}");;
            --Wx*)
		xcode_translator_add_opt=("${xcode_translator_add_opt[@]}" "${arg#--Wx}");;
	    --Wn*)
		native_add_opt=("${native_add_opt[@]}" "${arg#--Wn}");;
            --Wb*)
		backend_add_opt=("${backend_add_opt[@]}" "${arg#--Wb}");;
            --Wl*)
		linker_add_opt=("${linker_add_opt[@]}" "${arg#--Wl}");;
	    --openmp|-omp)
		ENABLE_OPENMP=true;;
	    --xcalableacc|-xacc)
		ENABLE_XACC=true;;
	    --scalasca-all)
		ENABLE_SCALASCA_ALL=true;;
	    --scalasca)
		ENABLE_SCALASCA=true;;
	    --tlog-all)
		ENABLE_TLOG_ALL=true;;
	    --tlog)
		ENABLE_TLOG=true;;
            *)
		if [ "$OUTPUT_FLAG" = true ]; then
		    output_file=("${arg}")
		    OUTPUT_FLAG=false
		elif [[ "${arg}" =~ \.c$ ]]; then
                    c_files=("${c_files[@]}" "${arg}")
                elif [[ "${arg}" =~ \.a$ ]]; then
                    archive_files=("${archive_files[@]}" "${arg}")
                elif [[ "${arg}" =~ \.o$ ]]; then
                    obj_files=("${obj_files[@]}" "${arg}")
		else
		    other_args=("${other_args[@]}" "${arg}")
		fi;;
	esac
    done

    if test $OUTPUT_TEMPORAL = true -a $DRY_RUN = true; then
        omni_error_exit "cannot use both --tmp and --dry options at the same time."
    fi
}
